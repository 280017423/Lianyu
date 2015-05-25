package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.PictureModel;

/**
 * 用户相册列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class UserPhotoAdapter extends BaseAdapter {

	private static final int SPACE_VALUE = 10;
	private int mCountOfRow = 4; // 一行显示4个图片
	private List<PictureModel> mImgList;
	private ImageLoader mImageLoader;
	private Activity mActivity;
	private int mWidth;
	private DisplayImageOptions mOptions;

	/**
	 * 构造函数
	 * 
	 * @param act
	 *            上下文对象
	 * @param list
	 *            图片数据
	 * @param imageLoader
	 *            图片加载器
	 */
	public UserPhotoAdapter(Activity activity, List<PictureModel> list, ImageLoader imageLoader) {
		this.mActivity = activity;
		this.mImgList = list;
		this.mImageLoader = imageLoader;
		mOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer()).build();
		mWidth = UIUtil.getScreenWidth(activity);
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mImgList != null) {
			count = mImgList.size();
		}
		return count;
	}

	@Override
	public PictureModel getItem(int position) {
		return mImgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int width = (mWidth - UIUtil.dip2px(mActivity, SPACE_VALUE) * (mCountOfRow + 1)) / mCountOfRow;
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(mActivity, R.layout.view_user_photo_item, null);
			holder.mIvImage = (RoundCornerImageView) convertView.findViewById(R.id.iv_head_img);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		PictureModel model = mImgList.get(position);
		mImageLoader.displayImage(model.getPhoto(), holder.mIvImage, mOptions);
		LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = layoutParams.width;
		holder.mIvImage.setLayoutParams(layoutParams);
		return convertView;
	}

	class Holder {
		private RoundCornerImageView mIvImage;
	}

}
