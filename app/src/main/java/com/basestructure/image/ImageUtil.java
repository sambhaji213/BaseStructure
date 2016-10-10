package com.basestructure.image;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.basestructure.base.ApplicationClass;
import com.basestructure.util.AppFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

public class ImageUtil {
    public static final String TAG="ImageUtil";
    public static final String imageSizeFull="full";
    public static final String imageSizeThumb="thumb";
    public static final String imageSizeFit="fit";
    public static final String imageCoding=".jpg";

    private static ImageUtil m_imageUtil=null;

    public static ImageUtil getInstance() {
        if(m_imageUtil==null)
        {
            m_imageUtil= new ImageUtil();
            appContext = ApplicationClass.getAppContext();
        }
        return m_imageUtil;
    }
    private static Context appContext = ApplicationClass.getAppContext();

    private ImageUtil() {}

    public static Uri saveBitmapFile(Bitmap bitmapImage,
                                     String imageID){
        File directory=appContext.getFilesDir();
        String fileName=imageID+imageCoding;
        File filePath=new File(directory,fileName);

        if(filePath.exists())
            appContext.deleteFile(fileName);

        FileOutputStream fos;
        try {
            fos = appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return Uri.fromFile(filePath);
        }
        catch (FileNotFoundException e) {
            Log.d(TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(TAG, "io exception");
            e.printStackTrace();
        }

        return null;
    }

    public static void deleteFile(String filePath){
        File file=new File(filePath);
        if(file.exists()) {
            String fileName=file.getName();
            appContext.deleteFile(fileName);
        }
    }

    public static String saveBitmap(Bitmap bitmapImage, String imageId){
        File directory=appContext.getFilesDir();
        String fileName=imageId+imageCoding;
        File file=new File(directory,fileName);
        String filePath=file.getAbsolutePath();
        if(file.exists())
            appContext.deleteFile(fileName);

        FileOutputStream fos;
        try {
            fos = appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return 	filePath;
        }
        catch (FileNotFoundException e) {
            Log.d(TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(TAG, "io exception");
            e.printStackTrace();
        }

        return filePath;
    }

    public static Bitmap getBitmapFromUri(ContentResolver resolver,
                                          Uri imageUri){
        Bitmap bitmap =null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    resolver, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    static public File createEmptyImageFile(String imageId){

       File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String fileName=imageId+imageCoding;

        File file=new File(storageDir,fileName);
        if(file.exists())
            appContext.deleteFile(fileName);
        return file;
    }

    public static String saveBitmapFile(Bitmap bitmapImage,
                                        String imageID, String imageSize){

        File directory=appContext.getFilesDir();
        String fileName=imageID+imageSize+imageCoding;
        File filePath=new File(directory,fileName);

        if(filePath.exists())
            appContext.deleteFile(fileName);

        FileOutputStream fos;
        try {
            fos = appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return 	fileName;
        }
        catch (FileNotFoundException e) {
            Log.d(TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(TAG, "io exception");
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap getBitmap(
            String imageID )
    {
        return getBitmap(imageID, imageSizeFull);
    }

    public static Bitmap getBitmap(
            String imageID, String imageSize){

        File directory=appContext.getFilesDir();
        String fileName=imageID+imageSize+imageCoding;
        File filePath=new File(directory,fileName);

        if(!filePath.exists())
            return null;

        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = appContext.openFileInput(fileName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();
        }
        catch (FileNotFoundException e) {
            Log.d(TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(TAG, "io exception");
            e.printStackTrace();
        }
        return b;
    }
    /**
     * Saves Images to internal database, matching parameters will overwrite existing file.
     * imageSize should be of [imageSizeFull,imageSizeThumb,imageSizeFit] options.
     * imageId should be unique.
     */
    public static final String saveInputStreamToFile(InputStream inputImage,
                                                     String imageID, String imageSize){

        File directory=appContext.getFilesDir();
        String fileName=imageID+imageSize+imageCoding;
        File filePath=new File(directory,fileName);

        if(filePath.exists())
            appContext.deleteFile(fileName);

        try {
            final OutputStream output = new FileOutputStream(filePath);
            try {
                final byte[] buffer = new byte[1024];
                int read;

                while ((read = inputImage.read(buffer)) != -1)
                    output.write(buffer, 0, read);

                output.flush();
            } finally {
                output.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return directory.getAbsolutePath();
    }

    public final static String getSavedFileFromInternalStorage(String imageID,
                                                               String imageSize){
        File directory=appContext.getFilesDir();
        String fileName=imageID+imageSize+imageCoding;
        File filePath=new File(directory,fileName);

        if(filePath.exists())
            return filePath.getAbsolutePath();
        return null;
    }

    class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String imageID = "";
        private String imageSize=imageSizeFit;
        private int reqWidth;
        private int reqHeight;


        public BitmapWorkerTask(String rID, ImageView imageView, String resSize, int width, int height) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
            imageID=rID;
            imageSize=resSize;
            reqWidth=width;
            reqHeight=height;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Object... params) {
	    	
	    	/*Bitmap fitBmp=getBitmap(imageID,imageSize);
	    	if (fitBmp !=null)
	    		return fitBmp;*/
            Bitmap fitBmp=null;
            String fullFile= getSavedFileFromInternalStorage(imageID, imageSizeFull);
            if(fullFile!=null){

                Bitmap decodedBmp = ScalingUtilities.decodeResource(fullFile, reqWidth, reqHeight,
                        ScalingUtilities.ScalingLogic.CROP);

                if(decodedBmp !=null)
                {
                    fitBmp=ScalingUtilities.createScaledBitmap(decodedBmp, reqWidth,
                            reqHeight, ScalingUtilities.ScalingLogic.CROP);
                    saveBitmapFile(fitBmp, imageID, imageSizeFit);
                }
            }

            return fitBmp;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public static void  loadBitmap(String imageID, ImageView imageView, String imageSize, int width, int height) {
        BitmapWorkerTask task = getInstance().new BitmapWorkerTask(imageID,imageView,imageSize, width, height);
        task.execute();
    }

    public static Bitmap getSavedImage(String imageId, String Size){
        Bitmap fitBmp=getBitmap(imageId,Size);
        if (fitBmp !=null)
            return fitBmp;

        String fullFile= getSavedFileFromInternalStorage(imageId, imageSizeFull);
        if(fullFile!=null){

            Bitmap decodedBmp = ScalingUtilities.decodeResource(fullFile, 350, 350,
                    ScalingUtilities.ScalingLogic.CROP);

            if(decodedBmp !=null)
            {
                fitBmp=ScalingUtilities.createScaledBitmap(decodedBmp, 350,
                        350, ScalingUtilities.ScalingLogic.CROP);
                saveBitmapFile(fitBmp, imageId, imageSizeFit);
            }
        }
        return fitBmp;
    }

    public static Uri saveUriToBitmapFile(Uri uri, ContentResolver resolver,
                                          String imageID, String imageSize, int reqWidth, int reqHeight) {
        String filePath = null;
        Bitmap decodedBmp = ScalingUtilities.decodeUri(uri, resolver, reqWidth,
                reqHeight, ScalingUtilities.ScalingLogic.CROP);

        if (decodedBmp != null)
            return saveBitmapFile(decodedBmp, imageID);
        return null;
    }

    public static Bitmap gateUriContent(ContentResolver resolver, Context appContext, Uri uri,
                                        int dstWidth, int dstHeight, String imageID ) {
        return gateUriContent(resolver, appContext, uri, dstWidth,dstHeight,imageID,false);
    }

    public static Bitmap gateUriContent(ContentResolver resolver, Context appContext, Uri uri,
                                        int dstWidth, int dstHeight, String imageID,
                                        boolean preferImageSize ){
        Bitmap bitmap=null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        File originalFile = AppFileUtils.getFile(appContext, uri);

        InputStream inputStream = null;
        try {
            inputStream = resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        BitmapFactory.decodeStream(inputStream, null, options);
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        catch ( NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        try {
            inputStream = resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        if(options.outHeight==dstHeight && options.outWidth==dstWidth) {
            bitmap= BitmapFactory.decodeStream(inputStream);
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return bitmap;
        }

        if(preferImageSize){
            dstHeight=options.outHeight<dstHeight?options.outHeight:dstHeight;
            dstWidth=options.outWidth<dstWidth?options.outWidth:dstWidth;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = ScalingUtilities.calculateInSampleSize(
                options, dstWidth, dstHeight);


        Bitmap unscaledBitmap = BitmapFactory.decodeStream(inputStream,
                null, options);
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Rect srcRect = ScalingUtilities.calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, ScalingUtilities.ScalingLogic.CROP);
        Rect dstRect = ScalingUtilities.calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight,  ScalingUtilities.ScalingLogic.CROP);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        if(originalFile==null)
            return scaledBitmap;
        return ScalingUtilities.rotateBitmap(originalFile.getAbsolutePath(), scaledBitmap);
    }

    public static Uri saveUriContent(ContentResolver resolver, Context appContext, Uri uri,
                                     int dstWidth, int dstHeight, String imageID ){
        Uri filePathUri=null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        File originalFile = AppFileUtils.getFile(appContext, uri);

        InputStream inputStream = null;
        try {
            inputStream = resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        BitmapFactory.decodeStream(inputStream, null, options);
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        catch ( NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        if(options.outHeight<dstHeight && options.outWidth<dstWidth) {
            filePathUri = uri;
            return filePathUri;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = ScalingUtilities.calculateInSampleSize(
                options, dstWidth, dstHeight);

        try {
            inputStream = resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Bitmap unscaledBitmap = BitmapFactory.decodeStream(inputStream,
                null, options);
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Rect srcRect = ScalingUtilities.calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, ScalingUtilities.ScalingLogic.CROP);
        Rect dstRect = ScalingUtilities.calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight,  ScalingUtilities.ScalingLogic.CROP);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        if(originalFile!=null)
            scaledBitmap = ScalingUtilities.rotateBitmap(originalFile.getAbsolutePath(), scaledBitmap);

        File directory=appContext.getFilesDir();
        String fileName=imageID+".jpg";
        File filePath=new File(directory,fileName);

        if(!filePath.exists())
            appContext.deleteFile(fileName);

        FileOutputStream fos;
        try {
            fos = appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            filePathUri= Uri.fromFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePathUri;
    }

}
