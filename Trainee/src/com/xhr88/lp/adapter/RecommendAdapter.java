package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr88.lp.R;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.datamodel.RecommendListModel;

/**
 * 推荐列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class RecommendAdapter extends BaseAdapter {
	private static final int SPACE_VALUE = 10;
	private static final int NUM_COLUMNS = 2;
	private List<RecommendListModel> mRecommendListModels;
	private Context mContext;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	private OnClickListener mOnClickListener;
	private int mWidth;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param loader
	 *            图片加载器
	 * @param dataList
	 *            数据列表
	 * @param listener
	 *            按钮点击监听
	 */
	public RecommendAdapter(Activity activity, List<RecommendListModel> dataList, ImageLoader loader,
			OnClickListener listener) {
		this.mContext = activity;
		this.mRecommendListModels = dataList;
		this.mImageLoader = loader;
		mOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.build();
		mWidth = UIUtil.getScreenWidth(activity);
		mOnClickListener = listener;
	}

	@Override
	public int getCount() {
		if (mRecommendListModels != null && !mRecommendListModels.isEmpty()) {
			return mRecommendListModels.size();
		}
		return 0;
	}

	@Override
	public RecommendListModel getItem(int position) {
		if (mRecommendListModels != null) {
			return mRecommendListModels.get(position);
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
			convertView = View.inflate(mContext, R.layout.view_recommend_item, null);
			holder.mIvImg = (RoundCornerImageView) convertView.findViewById(R.id.iv_img);
			holder.mIvPlay = (ImageView) convertView.findViewById(R.id.iv_play);
			holder.mIvLevel = (ImageView) convertView.findViewById(R.id.iv_level);
			holder.mTvContent = (TextView) convertView.findViewById(R.id.tv_content);
			holder.mBtnNickName = (Button) convertView.findViewById(R.id.btn_nickname);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final RecommendListModel model = getItem(position);
		holder.mIvLevel.setImageResource(ConstantSet.getLevelIcons(model.getLevel()));
		String imgUrl = model.getResource();
		mImageLoader.displayImage(imgUrl, holder.mIvImg, mOptions);

		if (model.isVideo()) {
			UIUtil.setViewVisible(holder.mIvPlay);
		} else {
			UIUtil.setViewGone(holder.mIvPlay);
		}

		holder.mTvContent.setText(model.getContent());

		holder.mBtnNickName.setTag(model);
		holder.mBtnNickName.setText(model.getNickname());
		if (null != mOnClickListener) {
			holder.mBtnNickName.setOnClickListener(mOnClickListener);
		}

		LayoutParams layoutParams = holder.mIvImg.getLayoutParams();
		layoutParams.width = (mWidth - UIUtil.dip2px(mContext, SPACE_VALUE) * (NUM_COLUMNS + 1)) / NUM_COLUMNS;
		layoutParams.height = layoutParams.width;
		holder.mIvImg.setLayoutParams(layoutParams);
		return convertView;
	}

	class viewHode {
		RoundCornerImageView mIvImg;
		ImageView mIvPlay;
		ImageView mIvLevel;
		TextView mTvContent;
		Button mBtnNickName;
	}
}