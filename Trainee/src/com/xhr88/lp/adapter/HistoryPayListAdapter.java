package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.HistoryPayModel;

/**
 * 历史支付列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class HistoryPayListAdapter extends BaseAdapter {
	private List<HistoryPayModel> mRecommendListModels;
	private Context mContext;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public HistoryPayListAdapter(Activity activity, List<HistoryPayModel> dataList) {
		this.mContext = activity;
		this.mRecommendListModels = dataList;
	}

	@Override
	public int getCount() {
		if (mRecommendListModels != null && !mRecommendListModels.isEmpty()) {
			return mRecommendListModels.size();
		}
		return 0;
	}

	@Override
	public HistoryPayModel getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.view_history_pay_item, null);
			holder.mTvPayDate = (TextView) convertView.findViewById(R.id.tv_pay_date);
			holder.mTvPayTime = (TextView) convertView.findViewById(R.id.tv_pay_time);
			holder.mTvPayInfo = (TextView) convertView.findViewById(R.id.tv_pay_info);
			holder.mTvCoin = (TextView) convertView.findViewById(R.id.tv_pay_coin);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final HistoryPayModel model = getItem(position);
		holder.mTvPayDate.setText(model.getDisplayPaydate());
		holder.mTvPayTime.setText(model.getDisplayPaytime());
		holder.mTvPayInfo.setText(model.getInfo());
		holder.mTvCoin.setText(model.getCoin());
		return convertView;
	}

	class viewHode {
		TextView mTvPayDate;
		TextView mTvPayTime;
		TextView mTvPayInfo;
		TextView mTvCoin;
	}
}