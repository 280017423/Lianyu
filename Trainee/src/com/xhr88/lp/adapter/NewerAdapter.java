package com.xhr88.lp.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.xhr88.lp.R;

/**
 * 新手引导界面适配器
 * 
 * @version
 * @author zou.sq
 */
public class NewerAdapter extends PagerAdapter {

	private Activity mContext;
	private int[] mImages;

	/**
	 * 构造函数
	 * 
	 * @param activity
	 *            上下文
	 */
	public NewerAdapter(Activity activity, int[] images) {
		this.mContext = activity;
		this.mImages = images;
	}

	/**
	 * 
	 * @return 返回默认大小
	 */
	@Override
	public int getCount() {
		if (null == mImages) {
			return 0;
		}
		return mImages.length;
	}

	/**
	 * 
	 * @return 返回真实大小
	 */
	public int getSize() {
		if (mImages != null) {
			return mImages.length;
		} else {
			return 0;
		}
	}

	protected class Item {
	}

	@Override
	public Object instantiateItem(View viewPager, int position) {

		View itemView = View.inflate(mContext, R.layout.view_newer_guiding_item, null);
		ImageView ivBg = (ImageView) itemView.findViewById(R.id.iv_bg);
		if (mImages.length > 0) {
			ivBg.setImageResource(mImages[position % mImages.length]);
		}
		((ViewPager) viewPager).addView(itemView);
		return itemView;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	@Override
	public void finishUpdate(View arg0) {

	}
}