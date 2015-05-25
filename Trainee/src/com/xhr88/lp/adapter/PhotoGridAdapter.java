package com.xhr88.lp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xhr88.lp.R;
import com.xhr88.lp.model.viewmodel.ImageItemModel;
import com.xhr88.lp.util.BitmapCache;
import com.xhr88.lp.util.BitmapCache.ImageCallback;

public class PhotoGridAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();
	private int mMaxImgNum;
	private Context mContext;
	private ArrayList<ImageItemModel> mImageList;
	private BitmapCache mCache;
	private ItemCallback itemCallback;
	private ArrayList<ImageItemModel> mHasChooseList;

	private ImageCallback imgCallback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				}
			}
		}
	};

	public PhotoGridAdapter(Context context, ArrayList<ImageItemModel> list, ArrayList<ImageItemModel> chooseList) {
		this.mContext = context;
		mImageList = list;
		mCache = new BitmapCache();
		mHasChooseList = chooseList;
	}

	public void setItemCallback(ItemCallback itemCallback) {
		this.itemCallback = itemCallback;
	}

	public void setMaxCount(int maxNum) {
		mMaxImgNum = maxNum;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mImageList != null) {
			count = mImageList.size();
		}
		return count;
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
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(mContext, R.layout.view_bucket_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final ImageItemModel item = mImageList.get(position);

		holder.iv.setTag(item.imagePath);
		mCache.displayBmp(holder.iv, item.imagePath, imgCallback);

		if (item.isSelected) {
			holder.selected.setImageResource(R.drawable.icon_data_select);
		} else {
			holder.selected.setImageResource(0x00000000);
		}
		holder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mHasChooseList.size() >= mMaxImgNum && !item.isSelected) {
					return;
				}
				item.isSelected = !item.isSelected;
				if (item.isSelected) {
					holder.selected.setImageResource(R.drawable.icon_data_select);
				} else {
					holder.selected.setImageResource(0x00000000);
				}
				if (itemCallback != null) {
					itemCallback.onChoose(position, item.isSelected);
				}
			}
		});

		return convertView;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
	}

	public interface ItemCallback {
		public void onChoose(int position, boolean choosed);
	}
}
