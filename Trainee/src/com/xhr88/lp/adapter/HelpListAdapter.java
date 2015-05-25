package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.HelpListModel;

/**
 * 帮助列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class HelpListAdapter extends BaseAdapter {
	private List<HelpListModel> mRecommendListModels;
	private Context mContext;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public HelpListAdapter(Activity activity, List<HelpListModel> dataList) {
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
	public HelpListModel getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.view_help_list_item, null);
			holder.mTvHelpTitle = (TextView) convertView.findViewById(R.id.tv_help_title);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final HelpListModel model = getItem(position);
		holder.mTvHelpTitle.setText(model.getTitle());

		return convertView;
	}

	class viewHode {
		TextView mTvHelpTitle;
	}
}