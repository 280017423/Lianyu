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
 * 用户信息分类适配器
 * 
 * @version
 * @author
 * 
 */
public class UserCateAdapter extends BaseAdapter {

	private List<UserCategoryModel> mRecommendListModels;
	private Context mContext;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public UserCateAdapter(Activity activity, List<UserCategoryModel> dataList) {
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
	public UserCategoryModel getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.view_recommend_filter_item, null);
			holder.mTvNickName = (TextView) convertView.findViewById(R.id.tv_cate_name);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final UserCategoryModel model = getItem(position);
		holder.mTvNickName.setText(model.getCategoryname());
		if (model.getIsChecked()) {
			holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_pressed);
			holder.mTvNickName.setTextColor(Color.WHITE);
			if (position == 0) {
				if (mRecommendListModels.size() <= 3)
					holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_press);
				else
					holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_press_tl);
			} else if (position == 2) {
				if (mRecommendListModels.size() <= 3)
					holder.mTvNickName.setBackgroundResource(R.drawable.recommend_woman_press);
				else
					holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_press_tr);
			} else if (position == mRecommendListModels.size() - 1 && position % 3 == 2) {
				holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_press_br);
			} else if (position % 3 == 0 && mRecommendListModels.size() - position <= 3) {
				holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_press_bl);
			} else {
				holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_pressed);
			}
		} else {
			holder.mTvNickName.setTextColor(Color.parseColor("#666666"));
			if (position == 0) {
				if (mRecommendListModels.size() <= 3)
					holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_normal_corner);
				else
					holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_normal_tl);

			} else if (position == 2) {
				if (mRecommendListModels.size() <= 3)
					holder.mTvNickName.setBackgroundResource(R.drawable.recommend_woman_normal_corner);
				else
					holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_normal_tr);
			} else if (position == mRecommendListModels.size() - 1 && position % 3 == 2) {
				holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_normal_br);
			} else if (position % 3 == 0 && mRecommendListModels.size() - position <= 3) {
				holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_normal_bl);
			} else {
				holder.mTvNickName.setBackgroundResource(R.drawable.recommend_man_normal);
			}
		}

		return convertView;
	}

	class viewHode {
		TextView mTvNickName;
	}

}