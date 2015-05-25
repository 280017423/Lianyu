package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.HistoryServiceModel;
import com.xhr88.lp.util.DateUtil;

/**
 * 历史服务列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class HistoryServiceListAdapter extends BaseAdapter {
	private List<HistoryServiceModel> mRecommendListModels;
	private Context mContext;
	private ImageLoader mImageLoader;
	private OnClickListener mOnClickListener;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public HistoryServiceListAdapter(Activity activity, List<HistoryServiceModel> dataList, ImageLoader imageLoader,
			OnClickListener listener) {
		this.mContext = activity;
		this.mRecommendListModels = dataList;
		mImageLoader = imageLoader;
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
	public HistoryServiceModel getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.view_history_service_item, null);
			holder.mIvHeadImg = (RoundCornerImageView) convertView.findViewById(R.id.iv_head_img);
			holder.mIvStatus = (ImageView) convertView.findViewById(R.id.iv_transfered);
			holder.mTvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
			holder.mTvDate = (TextView) convertView.findViewById(R.id.tv_date);
			holder.mTvCoin = (TextView) convertView.findViewById(R.id.tv_coin);
			holder.mTvStatus = (TextView) convertView.findViewById(R.id.tv_transfer_status);
			holder.mServiceBar = (RatingBar) convertView.findViewById(R.id.service_ratingbar);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final HistoryServiceModel model = getItem(position);
		holder.mIvHeadImg.setTag(model);
		holder.mIvHeadImg.setOnClickListener(mOnClickListener);
		int imageRes = R.drawable.default_man_head_bg;
		if (1 != model.getSex()) {
			imageRes = R.drawable.default_woman_head_bg;
		}
		mImageLoader.displayImage(model.getHeadimg(), holder.mIvHeadImg, new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageRes)
				.build());
		holder.mTvNickName.setText(model.getNickname());
		holder.mTvDate.setText(DateUtil.formatDateTime(model.getStarttime()));
		holder.mTvCoin.setText(model.getCoin());
		int status = model.getStatus();
		if (1 == status) {
			holder.mTvStatus.setText("未到账");
			holder.mTvStatus.setTextColor(Color.parseColor("#996600"));
			holder.mIvStatus.setImageResource(R.drawable.icon_transfering);
		} else {
			holder.mTvStatus.setText("已到账");
			holder.mTvStatus.setTextColor(Color.parseColor("#00cc00"));
			holder.mIvStatus.setImageResource(R.drawable.icon_transfered);
		}
		holder.mServiceBar.setRating(model.getEstimate());
		return convertView;
	}

	class viewHode {
		RoundCornerImageView mIvHeadImg;
		ImageView mIvStatus;
		TextView mTvNickName;
		TextView mTvDate;
		TextView mTvCoin;
		TextView mTvStatus;
		RatingBar mServiceBar;
	}
}