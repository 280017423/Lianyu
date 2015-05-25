package com.xhr88.lp.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.OnLineImageDetailActivity;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.datamodel.CommentModel;
import com.xhr88.lp.model.datamodel.TrendsListModel;
import com.xhr88.lp.util.DateUtil;
import com.xhr88.lp.widget.MGridView;
import com.xhr88.lp.widget.MListView;

public class MyTrendsListAdapter extends BaseAdapter {

	private List<TrendsListModel> mDynamicListModels;
	private Activity mContext;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	private OnClickListener mOnClickListener;
	private OnItemClickListener mOnItemClickListener;
	private int mWidth;

	public MyTrendsListAdapter(Activity activity, List<TrendsListModel> dataModel, ImageLoader loader,
			OnClickListener listener, OnItemClickListener onItemClickListener) {
		this.mContext = activity;
		this.mDynamicListModels = dataModel;
		this.mImageLoader = loader;
		mOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer()).build();
		mWidth = UIUtil.getScreenWidth(activity);
		mOnClickListener = listener;
		mOnItemClickListener = onItemClickListener;
	}

	@Override
	public int getCount() {
		if (mDynamicListModels != null && !mDynamicListModels.isEmpty()) {
			return mDynamicListModels.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mDynamicListModels != null && !mDynamicListModels.isEmpty()) {
			return mDynamicListModels.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.view_my_trends_list_item, null);

			viewHolder.mTvDate = (TextView) convertView.findViewById(R.id.tv_date);
			viewHolder.mTvMonth = (TextView) convertView.findViewById(R.id.tv_month);
			viewHolder.mTvContent = (TextView) convertView.findViewById(R.id.tv_trend_content);
			viewHolder.mIvOne = (ImageView) convertView.findViewById(R.id.iv_one_pic);
			viewHolder.mBtnLike = (Button) convertView.findViewById(R.id.btn_dynamic_like);
			viewHolder.mBtnComment = (Button) convertView.findViewById(R.id.btn_dynamic_comment);
			viewHolder.mGvMorePic = (MGridView) convertView.findViewById(R.id.gv_more_pic);
			viewHolder.mTvCommentTime = (TextView) convertView.findViewById(R.id.tv_comment_time);

			viewHolder.mListView = (MListView) convertView.findViewById(R.id.my_comment_list);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		UIUtil.setViewWidth(viewHolder.mTvDate, mWidth / 5);
		final TrendsListModel model = (TrendsListModel) getItem(position);
		viewHolder.mTvDate.setText(DateUtil.formatTrendsDate(model.getCreatetime()));
		viewHolder.mTvMonth.setText(DateUtil.getMonth(model.getCreatetime()));
//		viewHolder.mBtnComment.setTag(model);
		viewHolder.mBtnComment.setTag(R.id.tag1, model);
		
		viewHolder.mBtnComment.setOnClickListener(mOnClickListener);
		try {
			viewHolder.mTvCommentTime.setText(DateUtil.getCommentTime(model.getCreatetime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (1 == model.getIsgood()) {
			viewHolder.mBtnLike.setEnabled(false);
			viewHolder.mBtnLike.setText("已喜欢");
			viewHolder.mBtnLike.setCompoundDrawables(getArrowDrawable(R.drawable.dynamic_liked), null, null, null);
		} else {
			viewHolder.mBtnLike.setEnabled(true);
			viewHolder.mBtnLike.setText("喜欢");
			viewHolder.mBtnLike.setTag(model);
			viewHolder.mBtnLike.setCompoundDrawables(getArrowDrawable(R.drawable.dynamic_item_like), null, null, null);
			viewHolder.mBtnLike.setOnClickListener(mOnClickListener);
		}

		List<CommentModel> commentModels = model.getCommentlist();
		if (null == commentModels || commentModels.isEmpty()) {
			viewHolder.mListView.setVisibility(View.GONE);
		} else {
			viewHolder.mListView.setVisibility(View.VISIBLE);
			TrendsListCommentAdapter commentAdapter = new TrendsListCommentAdapter(mContext, commentModels);
			viewHolder.mListView.setAdapter(commentAdapter);
			viewHolder.mListView.setOnItemClickListener(mOnItemClickListener);
			viewHolder.mBtnComment.setTag(R.id.tag2, commentAdapter);
		}
		viewHolder.mTvContent.setText(model.getContent());

		String[] resourceList = model.getResource().split(",");
		if (StringUtil.isNullOrEmpty(model.getResource())) {
			viewHolder.mGvMorePic.setVisibility(View.GONE);
			viewHolder.mIvOne.setVisibility(View.GONE);
		} else if (resourceList != null && resourceList.length == 1) {
			viewHolder.mGvMorePic.setVisibility(View.GONE);
			viewHolder.mIvOne.setVisibility(View.VISIBLE);

			LayoutParams layoutParams = viewHolder.mIvOne.getLayoutParams();
			layoutParams.width = mWidth * 2 / 5;
			layoutParams.height = layoutParams.width;
			viewHolder.mIvOne.setLayoutParams(layoutParams);

			mImageLoader.displayImage(resourceList[0], viewHolder.mIvOne, mOptions);
			viewHolder.mIvOne.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String[] resourceList = model.getBigresource().split(",");
					ArrayList<String> imageList = new ArrayList<String>();
					for (int i = 0; i < resourceList.length; i++) {
						imageList.add(resourceList[i]);
					}
					Intent intent = new Intent(mContext, OnLineImageDetailActivity.class);
					intent.putExtra(ConstantSet.ONLINE_IMAGE_DETAIL, imageList);
					mContext.startActivity(intent);
					mContext.overridePendingTransition(R.anim.activity_enter_scale, R.anim.activity_persistent);
				}
			});
		} else {
			viewHolder.mGvMorePic.setVisibility(View.VISIBLE);
			viewHolder.mIvOne.setVisibility(View.GONE);

			NewsThumbAdapter mNewsThumbAdapter = new NewsThumbAdapter(
					mContext, Arrays.asList(resourceList), mImageLoader);
			viewHolder.mGvMorePic.setAdapter(mNewsThumbAdapter);
			viewHolder.mGvMorePic.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String[] resourceList = model.getBigresource().split(",");
					ArrayList<String> imageList = new ArrayList<String>();
					for (int i = 0; i < resourceList.length; i++) {
						imageList.add(resourceList[i]);
					}
					Intent intent = new Intent(mContext, OnLineImageDetailActivity.class);
					intent.putExtra(ConstantSet.ONLINE_IMAGE_DETAIL, imageList);
					intent.putExtra("position", position);
					mContext.startActivity(intent);
					mContext.overridePendingTransition(R.anim.activity_enter_scale, R.anim.activity_persistent);
				}
			});
		}
		viewHolder.mTvContent.setText(model.getContent().toString());
		return convertView;
	}

	class ViewHolder {
		MGridView mGvMorePic;
		ImageView mIvOne;
		TextView mTvDate;
		TextView mTvMonth;
		TextView mTvContent;
		TextView mTvCommentTime;
		Button mBtnLike;
		Button mBtnComment;
		MListView mListView;
	}

	private Drawable getArrowDrawable(int id) {
		Drawable drawable = mContext.getResources().getDrawable(id);
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}
}
