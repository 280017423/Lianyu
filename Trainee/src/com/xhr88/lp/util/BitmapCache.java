package com.xhr88.lp.util;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

import com.xhr.framework.util.EvtLog;
import com.xhr88.lp.common.ConstantSet;

public class BitmapCache {
	private Handler mHandler = new Handler();

	private ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(2);
	private LruCache<String, Bitmap> mImageCache;
	{
		initMemoryCache();
	}

	void initMemoryCache() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		mImageCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				int size = bitmap.getRowBytes() * bitmap.getHeight() / 1024;
				return size;
			}
		};
	}

	private void put(String path, Bitmap bmp) {
		if (!TextUtils.isEmpty(path) && bmp != null) {
			mImageCache.put(path, bmp);
		}
	}

	private Bitmap get(String key) {
		try {
			return mImageCache.get(key);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public void displayBmp(final ImageView iv, final String sourcePath, final ImageCallback callback) {
		if (TextUtils.isEmpty(sourcePath)) {
			return;
		}
		iv.setImageBitmap(null);
		final Bitmap bmp = get(sourcePath);
		mFixedThreadPool.execute(new Runnable() {

			public void run() {
				if (bmp != null) {
					if (callback != null) {
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								EvtLog.d("aaa", "显示缓存");
								callback.imageLoad(iv, bmp, sourcePath);
							}
						}, 20);
					}
					return;
				}
				final Bitmap thumb = ImageUtil.getImageThumbnail(sourcePath, ConstantSet.THUMBNAIL_WIDTH,
						ConstantSet.THUMBNAIL_HEIFHT);
				put(sourcePath, thumb);
				if (callback != null) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							EvtLog.d("aaa", "生成缩略图");
							callback.imageLoad(iv, thumb, sourcePath);
						}
					}, 20);
				}
			}
		});
	}

	public static boolean isBitmapExist(String imgLocalPath) {
		File temp = new File(imgLocalPath);
		if (temp.exists() && temp.isFile() && temp.length() > 100) {
			return true;
		}
		return false;
	}

	public interface ImageCallback {
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params);
	}
}
