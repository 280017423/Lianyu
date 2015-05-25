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
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.xhr.framework.widget.photoview.PhotoView;
import com.xhr.framework.widget.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.xhr88.lp.util.ImageUtil;

/**
 * 照片预览适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class PhotoPreviewAdapter extends PagerAdapter {

	private List<String> mNewsList;
	private Activity mContext;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public PhotoPreviewAdapter(Activity context, ArrayList<String> dataList) {
		this.mContext = context;
		this.mNewsList = dataList;
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
	public View instantiateItem(ViewGroup container, int position) {
		String imgUrl = getItem(position);
		final PhotoView photoView = new PhotoView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		photoView.setLayoutParams(params);
		photoView.setScaleType(ScaleType.FIT_CENTER);
		photoView.setImageBitmap(ImageUtil.getLoacalBitmap(imgUrl));
		container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		photoView.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View view, float x, float y) {
				mContext.finish();
			}
		});

		return photoView;
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
