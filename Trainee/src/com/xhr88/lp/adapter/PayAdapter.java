package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.PayModel;

/**
 * 支付列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class PayAdapter extends BaseAdapter {
	private List<PayModel> mRecommendListModels;
	private Context mContext;
	private OnClickListener mOnClickListener;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public PayAdapter(Activity activity, List<PayModel> dataList, OnClickListener listener) {
		this.mContext = activity;
		this.mRecommendListModels = dataList;
		this.mOnClickListener = listener;
	}

	@Override
	public int getCount() {
		if (mRecommendListModels != null && !mRecommendListModels.isEmpty()) {
			return mRecommendListModels.size();
		}
		return 0;
	}

	@Override
	public PayModel getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.view_pay_item, null);
			holder.mTvPayTitle = (TextView) convertView.findViewById(R.id.tv_pay_title);
			holder.mTvPayPrice = (TextView) convertView.findViewById(R.id.tv_pay_price);
			holder.mTvPayInfo = (TextView) convertView.findViewById(R.id.tv_pay_info);
			holder.mBtnBuy = (Button) convertView.findViewById(R.id.btn_buy);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}
		final PayModel model = getItem(position);
		holder.mBtnBuy.setTag(model);
		holder.mBtnBuy.setOnClickListener(mOnClickListener);
		holder.mTvPayTitle.setText("￥" + model.getMoney());
		holder.mTvPayInfo.setText(model.getInfo());
		holder.mTvPayPrice.setText("" + model.getCoin());
		return convertView;
	}

	class viewHode {
		TextView mTvPayTitle;
		TextView mTvPayPrice;
		TextView mTvPayInfo;
		Button mBtnBuy;
	}
}