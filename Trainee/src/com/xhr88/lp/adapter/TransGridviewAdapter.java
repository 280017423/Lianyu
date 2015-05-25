package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.listener.ImageGridItemListener;
import com.xhr88.lp.model.viewmodel.ImageItemModel;
import com.xhr88.lp.util.ImageUtil;

/**
 * 添加图片列表适配器
 * 
 * @author 
 * @since 
 * @version 
 */
public class TransGridviewAdapter extends BaseAdapter {

	private static final int SPACE_VALUE = 10;
	private int mCountOfRow = 4; // 一行显示4个图片
	private int mMaxItem = 4; // 最多显示多少图片
	private List<ImageItemModel> mImgList;
	private boolean mNeedAddImage = true;
	private ImageGridItemListener mItemCallback;
	private Activity mActivity;
	private int mWidth;

	/**
	 * 构造函数
	 * 
	 * @param act
	 *            上下文对象
	 * @param list
	 *            图片数据
	 * @param itemCallback
	 *            删除按钮回调函数
	 */
	public TransGridviewAdapter(Activity activity, List<ImageItemModel> list, ImageGridItemListener itemCallback) {
		this.mActivity = activity;
		this.mImgList = list;
		this.mItemCallback = itemCallback;
		mWidth = UIUtil.getScreenWidth(activity);
	}
	
	public void setMaxCount(int maxNum) {
		mMaxItem = maxNum;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mImgList != null) {
			count = mImgList.size();
		}

		int countBase = mNeedAddImage ? 1 : 0;
		if (count + countBase > mMaxItem) {
			return mMaxItem;
		} else {
			return count + countBase;
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int width = (mWidth - UIUtil.dip2px(mActivity, SPACE_VALUE) * (mCountOfRow + 1)) / mCountOfRow;
		// 添加图片的标志
		if (isClickAddPhoto(position)) {
			final LastHolder holder;
			if (convertView == null || convertView.getId() != R.id.item_image_grid2) {
				holder = new LastHolder();
				convertView = View.inflate(mActivity, R.layout.item_image_grid2, null);
				holder.mIvImage = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(holder);
			} else {
				holder = (LastHolder) convertView.getTag();
			}
			holder.mIvImage.setImageBitmap(null);
			holder.mIvImage.setImageResource(R.drawable.btn_add_photo_bg);

			holder.mIvImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mItemCallback != null) {
						if (isClickAddPhoto(position)) {
							mItemCallback.onAddPhotoClick();
						}
					}
				}
			});
			LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
			layoutParams.width = width;
			layoutParams.height = layoutParams.width;
			holder.mIvImage.setLayoutParams(layoutParams);
		} else {
			// 展示的图片
			final Holder holder;
			if (convertView == null || convertView.getId() != R.id.item_image_grid) {
				holder = new Holder();
				convertView = View.inflate(mActivity, R.layout.item_image_grid, null);
				holder.mIvImage = (ImageView) convertView.findViewById(R.id.image);
				holder.mIvDel = (ImageView) convertView.findViewById(R.id.iv_del);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			ImageItemModel item = mImgList.get(position);

			if (!StringUtil.isNullOrEmpty(item.imagePath)) {
				holder.mIvImage.setTag(item);
				holder.mIvImage.setImageBitmap(null);
				Bitmap bmp = ImageUtil.getImageThumbnail(item.imagePath, width, width);
				if (bmp != null) {
					holder.mIvImage.setImageBitmap(bmp);
				}
			}
			holder.mIvDel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mItemCallback != null) {
						mItemCallback.onItemDelClick(position);
					}
				}
			});
			LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
			layoutParams.width = width;
			layoutParams.height = layoutParams.width;
			holder.mIvImage.setLayoutParams(layoutParams);
		}

		return convertView;
	}

	boolean isClickAddPhoto(int position) {
		return mNeedAddImage && position == getCount() - 1 && mImgList != null && mImgList.size() < mMaxItem;
	}

	class Holder {
		private ImageView mIvImage;
		private ImageView mIvDel;
	}

	class LastHolder {
		private ImageView mIvImage;
	}

}
