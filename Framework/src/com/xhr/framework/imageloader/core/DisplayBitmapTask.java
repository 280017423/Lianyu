package com.xhr.framework.imageloader.core;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xhr.framework.imageloader.core.assist.ImageLoadingListener;
import com.xhr.framework.imageloader.core.display.BitmapDisplayer;

/**
 * Displays bitmap in {@link ImageView}. Must be called on UI thread.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoadingListener
 */
final class DisplayBitmapTask implements Runnable {

	private final Bitmap bitmap;
	private final ImageView imageView;
	private final BitmapDisplayer bitmapDisplayer;
	private final ImageLoadingListener listener;

	public DisplayBitmapTask(Bitmap bitmap, ImageView imageView, BitmapDisplayer bitmapDisplayer,
			ImageLoadingListener listener) {
		this.bitmap = bitmap;
		this.imageView = imageView;
		this.bitmapDisplayer = bitmapDisplayer;
		this.listener = listener;
	}

	@Override
	public void run() {
		Bitmap displayedBitmap = bitmapDisplayer.display(bitmap, imageView);
		listener.onLoadingComplete(displayedBitmap);
	}
}
