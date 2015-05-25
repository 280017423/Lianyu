package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr88.lp.R;
import com.xhr88.lp.model.viewmodel.ImageAlbumModel;
import com.xhr88.lp.util.BitmapCache;
import com.xhr88.lp.util.BitmapCache.ImageCallback;

/**
 * 相册列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class ImageAlbumAdapter extends BaseAdapter {
	private Activity mActivity;
	private List<ImageAlbumModel> mDataList;
	private BitmapCache mCache;
	private ImageCallback mCallback = new ImageCallback() {
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

	/**
	 * 构造函数
	 * 
	 * @param act
	 *            上下文对象
	 * @param list
	 *            相册数据
	 */
	public ImageAlbumAdapter(Activity act, List<ImageAlbumModel> list) {
		mActivity = act;
		mDataList = list;
		mCache = new BitmapCache();
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mDataList != null) {
			count = mDataList.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(mActivity, R.layout.view_album_item, null);
			holder.mIvBucket = (ImageView) convertView.findViewById(R.id.image);
			holder.mTvName = (TextView) convertView.findViewById(R.id.name);
			holder.mTvCount = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		ImageAlbumModel item = mDataList.get(position);

		holder.mTvName.setText(item.bucketName);
		holder.mTvCount.setText("(" + item.count + ")");
		if (item.imageList != null && !item.imageList.isEmpty()) {
			String sourcePath = item.imageList.get(0).imagePath;
			holder.mIvBucket.setTag(sourcePath);
			mCache.displayBmp(holder.mIvBucket, sourcePath, mCallback);
		} else {
			holder.mIvBucket.setImageBitmap(null);
		}

		return convertView;
	}

	class Holder {
		private ImageView mIvBucket;
		private TextView mTvName;
		private TextView mTvCount;
	}
}
