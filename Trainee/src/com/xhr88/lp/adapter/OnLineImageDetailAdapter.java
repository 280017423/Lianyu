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
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.assist.FailReason;
import com.xhr.framework.imageloader.core.assist.ImageLoadingListener;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.widget.photoview.PhotoView;
import com.xhr.framework.widget.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.xhr88.lp.R;
import com.xhr88.lp.util.AnimationUtil;

/**
 * 新闻列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class OnLineImageDetailAdapter extends PagerAdapter {

	private List<String> mNewsList;
	private List<String> mImgSmallList;
	private Activity mContext;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	// private LoadingUpView mLoadingUpView;
	private boolean isNeedFinish = true;

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
	public OnLineImageDetailAdapter(Activity context, ArrayList<String> dataList, ImageLoader loader, List<String> list) {
		this.mContext = context;
		this.mNewsList = dataList;
		this.mImageLoader = loader;
		this.mImgSmallList = list;
		mOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer()).build();
		// mLoadingUpView = new LoadingUpView(mContext);
	}

	@Override
	public int getCount() {
		if (null == mNewsList) {
			return 0;
		}
		return mNewsList.size();
	}

	public void setNeedFinish(boolean isNeedFinish) {
		this.isNeedFinish = isNeedFinish;
	}

	private String getItem(int position) {
		if (mNewsList != null) {
			return mNewsList.get(position);
		}
		return null;
	}

	@Override
	public View instantiateItem(ViewGroup container, final int position) {
		View view = mContext.getLayoutInflater().inflate(R.layout.loading_photo_view, null);
		final View layoutLoad = view.findViewById(R.id.layoutLoad);
		final PhotoView photoView = (PhotoView) view.findViewById(R.id.imgPic);
		final ImageView loadImg = (ImageView) view.findViewById(R.id.imgLoad);
		AnimationUtil.startRotaeAnimation(loadImg);
		// final PhotoView photoView = new PhotoView(mContext);
		// LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT);
		// photoView.setLayoutParams(params);
		// photoView.setScaleType(ScaleType.FIT_CENTER);
		// photoView.setImageResource(R.drawable.icon_img_loading);
		// String imgUrl = mImgSmallList.get(position).replaceAll("282", "50");
		String imgUrl = getItem(position);
		mImageLoader.displayImage(imgUrl, photoView, mOptions, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted() {
				// if (null != mLoadingUpView && !mLoadingUpView.isShowing()) {
				// mLoadingUpView.showPopup();
				// }
			}

			@Override
			public void onLoadingFailed(FailReason failReason) {
				// if (null != mLoadingUpView && mLoadingUpView.isShowing()) {
				// mLoadingUpView.dismiss();
				// }
				layoutLoad.setVisibility(View.GONE);
				loadImg.clearAnimation();
			}

			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				// if (null != mLoadingUpView && mLoadingUpView.isShowing()) {
				// mLoadingUpView.dismiss();
				// }
				layoutLoad.setVisibility(View.GONE);
				loadImg.clearAnimation();
			}

			@Override
			public void onLoadingCancelled() {
				// if (null != mLoadingUpView && mLoadingUpView.isShowing()) {
				// mLoadingUpView.dismiss();
				// }
				layoutLoad.setVisibility(View.GONE);
				loadImg.clearAnimation();
			}
		}, mImgSmallList == null ? null : mImgSmallList.get(position));

		// Now just add PhotoView to ViewPager and return it
		photoView.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View view, float x, float y) {
				if (isNeedFinish) {
					mContext.finish();
					mContext.overridePendingTransition(R.anim.activity_persistent, R.anim.activity_exit_scale);
				}
			}
		});
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
