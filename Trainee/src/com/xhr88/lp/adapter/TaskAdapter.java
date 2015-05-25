package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.TaskModel;

/**
 * 帮助列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class TaskAdapter extends BaseAdapter {
	private List<TaskModel> mRecommendListModels;
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
	public TaskAdapter(Activity activity, List<TaskModel> dataList, OnClickListener listener) {
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
	public TaskModel getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.view_task_item, null);
			holder.mIvTaskIcon = (ImageView) convertView.findViewById(R.id.iv_task_icon);
			holder.mTvTaskTitle = (TextView) convertView.findViewById(R.id.tv_task_title);
			holder.mTvTaskPrice = (TextView) convertView.findViewById(R.id.tv_sign_price);
			holder.mBtnSign = (Button) convertView.findViewById(R.id.btn_sign);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final TaskModel model = getItem(position);
		holder.mTvTaskTitle.setText(model.getTitle());
		int cate = model.getTaskid();
		int status = model.getGetstatus();
		holder.mBtnSign.setTag(model);
		holder.mBtnSign.setOnClickListener(mOnClickListener);
		if (1 == cate) {
			holder.mIvTaskIcon.setImageResource(R.drawable.icon_task_sign);
			if (2 == status) {
				holder.mBtnSign.setText("签到");
				holder.mBtnSign.setEnabled(true);
			} else {
				holder.mBtnSign.setEnabled(false);
				holder.mBtnSign.setText("已领取");
			}
		} else {
			holder.mIvTaskIcon.setImageResource(R.drawable.icon_task_share);
			if (2 == status) {
				holder.mBtnSign.setText("领取");
				holder.mBtnSign.setEnabled(true);
			} else if (0 == status) {
				holder.mBtnSign.setEnabled(false);
				holder.mBtnSign.setText("领取");
			} else if (1 == status) {
				holder.mBtnSign.setEnabled(false);
				holder.mBtnSign.setText("已领取");
			}
		}
		holder.mTvTaskPrice.setText("+" + model.getCoin());

		return convertView;
	}

	class viewHode {
		ImageView mIvTaskIcon;
		TextView mTvTaskTitle;
		TextView mTvTaskPrice;
		Button mBtnSign;
	}
}