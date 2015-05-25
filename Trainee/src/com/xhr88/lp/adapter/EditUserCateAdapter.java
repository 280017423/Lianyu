package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.UserCategoryModel;

/**
 * 编辑用户信息分类适配器
 * 
 * @version
 * @author
 * 
 */
public class EditUserCateAdapter extends BaseAdapter {

	private List<UserCategoryModel> mUserCates;
	private Context mContext;
	private String[] mColors;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public EditUserCateAdapter(Activity activity, List<UserCategoryModel> dataList) {
		this.mContext = activity;
		this.mUserCates = dataList;
		mColors = new String[] {
				"#F27DC4",
				"#B57DF2",
				"#A6E461",
				"#E99187",
				"#E0D14E",
				"#89B7E0",
				"#9CD988",
				"#C5CB7A",
				"#DF7182",
				"#7CDCAC",
				"#8E97D2", };
	}

	@Override
	public int getCount() {
		if (mUserCates != null && !mUserCates.isEmpty()) {
			return mUserCates.size();
		}
		return 0;
	}

	@Override
	public UserCategoryModel getItem(int position) {
		if (mUserCates != null) {
			return mUserCates.get(position);
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
			convertView = View.inflate(mContext, R.layout.view_recommend_filter_item, null);
			holder.mTvCateName = (TextView) convertView.findViewById(R.id.tv_cate_name);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final UserCategoryModel model = getItem(position);
		holder.mTvCateName.setText(model.getCategoryname());
		if (model.getIsChecked()) {
			holder.mTvCateName.setBackgroundColor(Color.parseColor(mColors[position % mColors.length]));
			holder.mTvCateName.setTextColor(Color.WHITE);
		} else {
			holder.mTvCateName.setTextColor(Color.parseColor("#999999"));
			holder.mTvCateName.setBackgroundColor(Color.parseColor("#e0e0e0"));
		}

		return convertView;
	}

	class viewHode {
		TextView mTvCateName;
	}

}