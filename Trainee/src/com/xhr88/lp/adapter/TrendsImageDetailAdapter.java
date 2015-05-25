/**
 * @Title: DishEmptyAdapter.java
 * @Project DCB
 * @Package com.pdw.dcb.ui.adapter
 * @Description: 沽清列表
 * @author zeng.ww
 * @date 2012-12-10 下午04:37:29
 * @version V1.0
 */
package com.xhr88.lp.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.assist.FailReason;
import com.xhr.framework.imageloader.core.assist.ImageLoadingListener;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.OnLineImageDetailActivity;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.util.AnimationUtil;

/**
 * 新闻列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class TrendsImageDetailAdapter extends PagerAdapter {

	private ArrayList<String> mNewsList;
	private ArrayList<String> mBigImgList;
	private Activity mContext;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	private LoadingUpView mLoadingUpView;
	private boolean mIsHideLoading;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param loader
	 *            图片加载器
	 * @param dataList
	 *            数据列表
	 */
	public TrendsImageDetailAdapter(Activity context, ArrayList<String> dataList, ArrayList<String> bigDataList,
			ImageLoader loader) {
		this.mContext = context;
		this.mNewsList = dataList;
		this.mBigImgList = bigDataList;
		this.mImageLoader = loader;
		mOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer()).build();
		mLoadingUpView = new LoadingUpView(mContext);
	}

	public void hideLoading(boolean hideLoading) {
		mIsHideLoading = hideLoading;
	}

	@Override
	public int getCount() {
		if (null == mNewsList) {
			return 0;
		}
		return mNewsList.size();
	}

	private String getItem(int position) {
		if (mNewsList != null) {
			return mNewsList.get(position);
		}
		return null;
	}

	@Override
	public View instantiateItem(ViewGroup container, final int position) {
		View view = mContext.getLayoutInflater().inflate(R.layout.loading_pic_view, null);
		final View layoutLoad = view.findViewById(R.id.layoutLoad);
		ImageView photoView = (ImageView) view.findViewById(R.id.imgPic);
		final ImageView loadImg = (ImageView) view.findViewById(R.id.imgLoad);
		AnimationUtil.startRotaeAnimation(loadImg);
		// final ImageView photoView = new ImageView(mContext);
		// LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT);
		// photoView.setLayoutParams(params);
		// photoView.setScaleType(ScaleType.FIT_XY);
		photoView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, OnLineImageDetailActivity.class);
				String[] ss = new String[mNewsList.size()];
				ss = mNewsList.toArray(ss);
				intent.putExtra(ConstantSet.ONLINE_IMAGE_DETAIL, mBigImgList);
				intent.putExtra(ConstantSet.ONLINE_IMAGE_SMALL, ss);
				intent.putExtra("position", position);
				mContext.startActivity(intent);
				mContext.overridePendingTransition(R.anim.activity_enter_scale, R.anim.activity_persistent);
			}
		});

		String imgUrl = getItem(position);
		mImageLoader.displayImage(imgUrl, photoView, mOptions, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted() {
				if (!mIsHideLoading && null != mLoadingUpView && !mLoadingUpView.isShowing()) {
					mLoadingUpView.showPopup();
				}
			}

			@Override
			public void onLoadingFailed(FailReason failReason) {
				if (!mIsHideLoading && null != mLoadingUpView && mLoadingUpView.isShowing()) {
					mLoadingUpView.dismiss();
				}
				layoutLoad.setVisibility(View.GONE);
				loadImg.clearAnimation();
			}

			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				if (!mIsHideLoading && null != mLoadingUpView && mLoadingUpView.isShowing()) {
					mLoadingUpView.dismiss();
				}
				layoutLoad.setVisibility(View.GONE);
				loadImg.clearAnimation();
			}

			@Override
			public void onLoadingCancelled() {
				if (!mIsHideLoading && null != mLoadingUpView && mLoadingUpView.isShowing()) {
					mLoadingUpView.dismiss();
				}
				layoutLoad.setVisibility(View.GONE);
				loadImg.clearAnimation();
			}
		});

		// Now just add PhotoView to ViewPager and return it
		container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
	}

}
