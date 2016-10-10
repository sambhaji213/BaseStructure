package com.basestructure.image;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.ImageView.ScaleType;

import com.basestructure.util.AppFileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/*
this logic taken from volley image parser please refer
*volley/src/main/java/com/android/volley/toolbox/ImageRequest.java
 *  */
public class ImageRequest  {

    private final Config mDecodeConfig;
    private final int mMaxWidth;
    private final int mMaxHeight;
    private ScaleType mScaleType;

    public ImageRequest(int maxWidth, int maxHeight,
                        ScaleType scaleType, Config decodeConfig ) {
        mDecodeConfig = decodeConfig;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mScaleType = scaleType;
    }

    public ImageRequest( int maxWidth, int maxHeight,
                         ScaleType scaleType ) {
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mScaleType = scaleType;
        mDecodeConfig=null;
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                           int actualSecondary, ScaleType scaleType) {

        // If no dominant value at all, just return the actual.
        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        // If ScaleType.FIT_XY fill the whole rectangle, ignore ratio.
        if (scaleType == ScaleType.FIT_XY) {
            if (maxPrimary == 0) {
                return actualPrimary;
            }
            return maxPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        // If ScaleType.CENTER_CROP fill the whole rectangle, preserve aspect ratio.
        if (scaleType == ScaleType.CENTER_CROP) {
            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        if ((resized * ratio) > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    public Bitmap getResizedBitmap(Bitmap tempBitmap, boolean scaleAlways ) {

        Bitmap bitmap=null;
        // If we have to resize this image, first get the natural bounds.
        int actualWidth = tempBitmap.getWidth();
        int actualHeight = tempBitmap.getHeight();

        // Then compute the dimensions we would ideally like to decode to.
        int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                actualWidth, actualHeight, mScaleType);
        int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                actualHeight, actualWidth, mScaleType);

        boolean doScale = scaleAlways || (tempBitmap.getWidth() > desiredWidth ||
                tempBitmap.getHeight() > desiredHeight);

        doScale = doScale && !(tempBitmap.getWidth() == desiredWidth &&
                tempBitmap.getHeight() == desiredHeight);

        // If necessary, scale down to the maximal acceptable size.
        if (doScale) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap,
                    desiredWidth, desiredHeight, true);
        } else {
            bitmap = tempBitmap;
        }

        return bitmap;
    }


    public class ImageResizeInfo{
        public Bitmap resizedBitmap=null;
        public String filePath="";
        public Boolean considerOriginalPath=false;
    }

    public ImageResizeInfo getResizedBitmap(ContentResolver resolver, Context appContext, Uri uri,
                                            boolean scaleAlways, boolean compress, int compressAmount ) {

        int maxHeight=mMaxHeight;
        int maxWidth=mMaxWidth;

        ImageResizeInfo resizeInfo = new ImageResizeInfo();
        File originalFile = AppFileUtils.getFile(appContext, uri);

        InputStream inputStream = null;
        try {
            inputStream = resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        if(uri.getPath()!=null){ /*if file size is less than 200 KB return original file*/
            Cursor returnCursor =
                    resolver.query(uri, null, null, null, null);

            int sizeIndex=0;
            if(returnCursor!=null) {
                sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                long fileSize= returnCursor.getLong(sizeIndex);
                returnCursor.close();
                if(fileSize<200*1000){
                    try {
                        if (inputStream != null) {
                            Bitmap originalBitmap =  BitmapFactory.decodeStream(inputStream);
                            inputStream.close();
                            resizeInfo.resizedBitmap=originalBitmap;
                            resizeInfo.filePath=originalFile.getAbsolutePath();
                            resizeInfo.considerOriginalPath=true;
                            return resizeInfo;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, decodeOptions);
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Bitmap bitmap = null;

        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        if(compress){
            //if compression is required that time change max width according to actual width
            if(actualWidth>maxWidth*3){
                compressAmount = 50;
               // maxWidth=(int)(maxWidth*1.3);
            }
            else if(actualWidth>maxWidth*2.0){
                compressAmount = 60;
              //  maxWidth=(int)(maxWidth*1.2);
            }
            else if(actualWidth>maxWidth*1.5){
                compressAmount = 70;
              //  maxWidth=(int)(maxWidth*1.1);
            }
            else if(actualWidth>maxWidth*1.2){
                compressAmount = 80;
               // maxWidth=actualWidth;
            }
            else if(actualWidth>maxWidth){
                compressAmount = 90;
            }
        }

        // Then compute the dimensions we would ideally like to decode to.
        int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                actualWidth, actualHeight, mScaleType);
        int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                actualHeight, actualWidth, mScaleType);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize =
                findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);


        try {
            inputStream = resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Bitmap tempBitmap =
                BitmapFactory.decodeStream(inputStream, null, decodeOptions);

        if (tempBitmap != null) {
            boolean doScale = scaleAlways || (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight);

            doScale = doScale && !(tempBitmap.getWidth() == desiredWidth &&
                    tempBitmap.getHeight() == desiredHeight);

            // If necessary, scale down to the maximal acceptable size.
            if (doScale) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }

        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Bitmap decoded=null;
        if(bitmap!=null && compress) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressAmount, out);
            if(out.toByteArray()!=null) {
                decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            }
        }

        if(originalFile==null)
            resizeInfo.resizedBitmap= decoded!=null?decoded:bitmap;
        else
            resizeInfo.resizedBitmap= ScalingUtilities.rotateBitmap(originalFile.getAbsolutePath(),
                    decoded!=null?decoded:bitmap);
        resizeInfo.filePath="";
        resizeInfo.considerOriginalPath=false;
        return resizeInfo;
    }

    private Bitmap doParse(byte[] data) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap = null;
        if (mMaxWidth == 0 && mMaxHeight == 0) {
            decodeOptions.inPreferredConfig = mDecodeConfig;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        } else {
            // If we have to resize this image, first get the natural bounds.
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                    actualWidth, actualHeight, mScaleType);
            int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                    actualHeight, actualWidth, mScaleType);

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
            // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
            decodeOptions.inSampleSize =
                    findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap =
                    BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }

        return bitmap;
    }

    // Visible for testing.
    static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }
}
