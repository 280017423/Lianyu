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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.UIUtil;
import com.xhr88.lp.R;

/**
 * 新闻列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class NewsThumbAdapter extends BaseAdapter {
	private static final int SPACE_VALUE = 10;
	private static final int NUM_COLUMNS = 3;
	private List<String> mNewsList;
	private Context mContext;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	private int mWidth;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 * @param loader
	 *            图片加载器
	 */
	public NewsThumbAdapter(Activity activity, List<String> dataList, ImageLoader loader) {
		this.mContext = activity;
		this.mNewsList = dataList;
		this.mImageLoader = loader;
		mOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer()).build();
		mWidth = UIUtil.getScreenWidth(activity) * 4 / 5;
	}

	@Override
	public int getCount() {
		if (mNewsList != null && !mNewsList.isEmpty()) {
			return mNewsList.size();
		}
		return 0;
	}

	@Override
	public String getItem(int position) {
		if (mNewsList != null) {
			return mNewsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHode holder = new viewHode();
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.view_news_thumb_item, null);
			holder.mIvImg = (ImageView) convertView.findViewById(R.id.iv_thumb_item);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}
		String imgUrl = getItem(position);
		mImageLoader.displayImage(imgUrl, holder.mIvImg, mOptions);

		LayoutParams layoutParams = holder.mIvImg.getLayoutParams();
		layoutParams.width = (mWidth - UIUtil.dip2px(mContext, SPACE_VALUE) * (NUM_COLUMNS + 1)) / NUM_COLUMNS;
		layoutParams.height = layoutParams.width;
		holder.mIvImg.setLayoutParams(layoutParams);

		return convertView;
	}

	class viewHode {
		ImageView mIvImg;
	}
}
