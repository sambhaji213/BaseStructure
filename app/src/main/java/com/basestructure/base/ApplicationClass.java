package com.basestructure.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.basestructure.R;
import com.basestructure.util.AppLoginSession;
import com.basestructure.util.AppLruBitmapCache;
import com.basestructure.util.AppNetworkImageView;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ApplicationClass extends Application {
    private static Context context;
	public static final String TAG = "Application";
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private static ApplicationClass mInstance;
	private AppLruBitmapCache mlruBitmapCache;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
 
	@Override
	public void onCreate() {
		super.onCreate();
        ApplicationClass.context = getApplicationContext();
		mInstance = this;

        // register to be informed of activities starting up
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity arg0, Bundle arg1) {
                // new activity created; force its orientation to portrait
                arg0.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityDestroyed(Activity arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivityPaused(Activity arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivityResumed(Activity arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivityStarted(Activity arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivityStopped(Activity arg0) {
                // TODO Auto-generated method stub

            }
        });
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
 
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static Context getAppContext() {
			return ApplicationClass.context;
	}
 
	public static synchronized ApplicationClass getInstance() {
		return mInstance;
	}


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
        {
            HurlStack stack = new HurlStack() {
                @Override
                public HttpResponse performRequest(Request<?> request, Map<String, String> headers)
                        throws IOException, AuthFailureError {
                    AppLoginSession.addSessionCookie(headers);
                    return super.performRequest(request, headers);
                }
            };
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), stack);
        }
        return mRequestQueue;
    }

    public void removeImageUrlFromCache(String url){
        if(mlruBitmapCache!=null) {
            List<String> keys = mlruBitmapCache.findMatchingKeys(url);
            for (String key : keys) {
                Bitmap bitmap = mlruBitmapCache.remove(key);
                mlruBitmapCache.removeKey(key);
            }
        }
    }

    public void replaceCacheBitmap(String url, Bitmap newBitmap){
        if(mlruBitmapCache!=null) {
            List<String> keys = mlruBitmapCache.findMatchingKeys(url);
            for (String key : keys) {
                Bitmap bitmap = mlruBitmapCache.remove(key);
                mlruBitmapCache.removeKey(key);
                mlruBitmapCache.putBitmap(key, newBitmap);
            }
        }
    }

    public Bitmap getBitmapFromCache(String url){
        if(mlruBitmapCache!=null) {
            List<String> keys = mlruBitmapCache.findMatchingKeys(url);
            for (String key : keys) {
                Bitmap bitmap= mlruBitmapCache.get(key);
                if(bitmap!=null)
                    return bitmap;
            }
        }
        return null;
    }

    public void loadImage(String imageUrl, final NetworkImageView imageView, final ProgressBar progressBar) {
        loadImage(imageUrl,  imageView, progressBar,  R.drawable.logo, null );
    }

    public void loadImage(String imageUrl, final NetworkImageView imageView, final ProgressBar progressBar,
                          final ImageView imageViewShadow) {
        loadImage(imageUrl,  imageView, progressBar, -1, imageViewShadow );
    }

    public void loadImage(String imageUrl, final NetworkImageView imageView,
                          final ProgressBar progressBar, final int defaultImageView,
                          final ImageView imageViewShadow ) {

        if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);

        if (StringUtils.isNotBlank(imageUrl)) {
            mImageLoader.get(imageUrl, new ImageLoader.ImageListener() {

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        if(progressBar!=null)
                            progressBar.setVisibility(View.GONE);
                        if(imageViewShadow!=null)
                            imageViewShadow.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(bitmap);
                    }
                    else if (defaultImageView>0)
                        imageView.setDefaultImageResId(defaultImageView);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponse ", error.toString());
                    if(progressBar!=null)
                        progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void loadImage(String imageUrl, final AppNetworkImageView imageView,
                          final ProgressBar progressBar, final int defaultImageView
            , final int errorImageResId, final Bitmap recycleBitmap ) {

        if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);

        if (StringUtils.isNotBlank(imageUrl)) {
            mImageLoader.get(imageUrl, new ImageLoader.ImageListener() {

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        if(progressBar!=null)
                            progressBar.setVisibility(View.GONE);
                        imageView.setImageBitmap(bitmap);
                        if(recycleBitmap!=null) {
                            recycleBitmap.recycle();
                        }
                    }
                    else if (defaultImageView>0)
                        imageView.setDefaultImageResId(defaultImageView);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponse ", error.toString());
                    if(progressBar!=null)
                        progressBar.setVisibility(View.GONE);
                    if (errorImageResId != 0) {
                        imageView.setImageResource(errorImageResId);
                    }
                }
            });
        }
    }

    public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
            mlruBitmapCache = new AppLruBitmapCache();
			mImageLoader = new ImageLoader(this.mRequestQueue,
                    mlruBitmapCache);
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests() {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(TAG);
		}
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}