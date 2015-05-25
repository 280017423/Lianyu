package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr88.lp.R;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.datamodel.SearchUserModel;

/**
 * 推荐列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class SearchListAdapter extends BaseAdapter {
	private List<SearchUserModel> mRecommendListModels;
	private Context mContext;
	private ImageLoader mImageLoader;
	private OnClickListener mOnClickListener;

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
	public SearchListAdapter(Activity activity, List<SearchUserModel> dataList, ImageLoader loader,
			OnClickListener listener) {
		this.mContext = activity;
		this.mRecommendListModels = dataList;
		this.mImageLoader = loader;
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
	public SearchUserModel getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.view_search_item, null);
			holder.mIvSex = (ImageView) convertView.findViewById(R.id.iv_sex);
			holder.mIvLevel = (ImageView) convertView.findViewById(R.id.iv_level);
			holder.mIvImg = (RoundCornerImageView) convertView.findViewById(R.id.iv_head_img);
			holder.mIvVideo = (ImageView) convertView.findViewById(R.id.iv_isvideo);
			holder.mIbtnRelation = (ImageButton) convertView.findViewById(R.id.ibtn_relation_fans);
			holder.mTvAge = (TextView) convertView.findViewById(R.id.tv_age);
			holder.mTvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
			holder.mTvConstellatory = (TextView) convertView.findViewById(R.id.tv_constellatory);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}
		final SearchUserModel model = getItem(position);
		holder.mIvLevel.setImageResource(ConstantSet.getLevelIcons(model.getLevel()));
		int imageRes = R.drawable.default_man_head_bg;
		if (1 == model.getSex()) {
			holder.mIvSex.setImageResource(R.drawable.icon_man);
		} else {
			imageRes = R.drawable.default_woman_head_bg;
			holder.mIvSex.setImageResource(R.drawable.icon_woman);
		}
		mImageLoader.displayImage(model.getHeadimg(), holder.mIvImg, new DisplayImageOptions.Builder().cacheInMemory()
				.cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageRes).build());
		if (1 == model.getIsvideo()) {
			UIUtil.setViewVisible(holder.mIvVideo);
		} else {
			UIUtil.setViewGone(holder.mIvVideo);
		}
		holder.mIbtnRelation.setTag(model);
		holder.mIbtnRelation.setOnClickListener(mOnClickListener);
		if (1 == model.getRelation()) {
			holder.mIbtnRelation.setBackgroundResource(R.drawable.icon_attentioned);
		} else if (2 == model.getRelation()) {
			holder.mIbtnRelation.setBackgroundResource(R.drawable.icon_attentioned_each_other);
		} else {
			holder.mIbtnRelation.setBackgroundResource(R.drawable.icon_no_attentioned);
		}
		try {
			holder.mTvAge.setText(model.getAge());
		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.mTvNickName.setText(model.getNickname());
		holder.mTvConstellatory.setText(model.getConstellatory());

		return convertView;
	}

	class viewHode {
		RoundCornerImageView mIvImg;
		ImageView mIvSex;
		ImageView mIvVideo;
		ImageView mIvLevel;
		ImageButton mIbtnRelation;
		TextView mTvAge;
		TextView mTvNickName;
		TextView mTvConstellatory;
	}
}