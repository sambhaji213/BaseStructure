package com.basestructure.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppLruBitmapCache extends LruCache<String, Bitmap> implements
		ImageCache {

	HashMap<String,Long> keyList = new HashMap<>();

	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		return cacheSize;
	}

	public AppLruBitmapCache() {
		this(getDefaultLruCacheSize());
	}

	public AppLruBitmapCache(int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
        keyList.put(url, System.currentTimeMillis());
		put(url, bitmap);
	}

    public void removeKey(String key){
        keyList.remove(key);
    }

    public List<String> findMatchingKeys(String keyToSearch){
        List<String> keys = new ArrayList<>();
        for(String key: keyList.keySet())
            if(StringUtils.contains(key, keyToSearch))
                keys.add(key);
        return keys;
    }
}
