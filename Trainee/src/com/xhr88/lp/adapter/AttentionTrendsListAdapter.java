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
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.OnLineImageDetailActivity;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.datamodel.AttentionTrendsModel;
import com.xhr88.lp.model.datamodel.CommentModel;
import com.xhr88.lp.util.DateUtil;
import com.xhr88.lp.widget.MGridView;
import com.xhr88.lp.widget.MListView;

public class AttentionTrendsListAdapter extends BaseAdapter {

	private List<AttentionTrendsModel> mAttentionTrendsModels;
	private Activity mContext;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	private OnClickListener mOnClickListener;
	private int mWidth;

	public AttentionTrendsListAdapter(Activity activity, List<AttentionTrendsModel> dataModel, ImageLoader loader,
			OnClickListener listener) {
		this.mContext = activity;
		this.mAttentionTrendsModels = dataModel;
		this.mImageLoader = loader;
		mOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer()).build();
		mWidth = UIUtil.getScreenWidth(activity);
		mOnClickListener = listener;
	}

	@Override
	public int getCount() {
		if (mAttentionTrendsModels != null && !mAttentionTrendsModels.isEmpty()) {
			return mAttentionTrendsModels.size();
		}
		return 0;
	}

	@Override
	public AttentionTrendsModel getItem(int position) {
		if (mAttentionTrendsModels != null && !mAttentionTrendsModels.isEmpty()) {
			return mAttentionTrendsModels.get(position);
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
			convertView = View.inflate(mContext, R.layout.view_attention_trends_list_item, null);
			viewHolder.mIvSex = (ImageView) convertView.findViewById(R.id.iv_sex_fans);
			viewHolder.mIbtnHead = (RoundCornerImageView) convertView.findViewById(R.id.ibtn_head_img);
			viewHolder.mTvContent = (TextView) convertView.findViewById(R.id.tv_trend_content);
			viewHolder.mIvOne = (ImageView) convertView.findViewById(R.id.iv_one_pic);
			viewHolder.mBtnLike = (Button) convertView.findViewById(R.id.btn_dynamic_like);
			viewHolder.mBtnComment = (Button) convertView.findViewById(R.id.btn_dynamic_comment);
			viewHolder.mIvVideo = (ImageView) convertView.findViewById(R.id.iv_isvideo_fans);
			viewHolder.mGvMorePic = (MGridView) convertView.findViewById(R.id.gv_more_pic);
			viewHolder.mTvCommentTime = (TextView) convertView.findViewById(R.id.tv_comment_time);
			viewHolder.mTvNickName = (TextView) convertView.findViewById(R.id.tv_nickname_fans);
			viewHolder.mIvLevel = (ImageView) convertView.findViewById(R.id.iv_level_fans);
			viewHolder.mListView = (MListView) convertView.findViewById(R.id.my_comment_list);
			viewHolder.mTvAge = (TextView) convertView.findViewById(R.id.tv_age_fans);
			viewHolder.mTvConstellatory = (TextView) convertView.findViewById(R.id.tv_constellatory_fans);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		UIUtil.setViewWidth(viewHolder.mIbtnHead, mWidth / 5);
		UIUtil.setViewHeight(viewHolder.mIbtnHead, mWidth / 5);
		final AttentionTrendsModel model = getItem(position);
		viewHolder.mIvLevel.setImageResource(ConstantSet.getLevelIcons(model.getLevel()));
		if (1 == model.getIsvideo()) {
			UIUtil.setViewVisible(viewHolder.mIvVideo);
		} else {
			UIUtil.setViewGone(viewHolder.mIvVideo);
		}
		int imageRes = R.drawable.default_man_head_bg;
		if (1 == model.getSex()) {
			viewHolder.mIvSex.setImageResource(R.drawable.icon_man);
		} else {
			imageRes = R.drawable.default_woman_head_bg;
			viewHolder.mIvSex.setImageResource(R.drawable.icon_woman);
		}
		try {
			viewHolder.mTvAge.setText(model.getAge() + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewHolder.mTvConstellatory.setText(model.getConstellatory());
		viewHolder.mTvNickName.setText(model.getNickname());
		mImageLoader.displayImage(model.getHeadimg(), viewHolder.mIbtnHead, new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageRes)
				.build());
		viewHolder.mBtnComment.setTag(R.id.tag1, model);
		viewHolder.mBtnComment.setTag(R.id.tag2, viewHolder.mListView);
		viewHolder.mIbtnHead.setTag(model);
		viewHolder.mBtnComment.setOnClickListener(mOnClickListener);
		viewHolder.mIbtnHead.setOnClickListener(mOnClickListener);
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
		}
		viewHolder.mTvContent.setText(model.getContent());
		final String[] resourceList = model.getResource().split(",");
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
					intent.putExtra(ConstantSet.ONLINE_IMAGE_SMALL, resourceList);
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
					String[] resourceList1 = model.getBigresource().split(",");
					ArrayList<String> imageList = new ArrayList<String>();
					for (int i = 0; i < resourceList1.length; i++) {
						imageList.add(resourceList1[i]);
					}
					Intent intent = new Intent(mContext, OnLineImageDetailActivity.class);
					intent.putExtra(ConstantSet.ONLINE_IMAGE_DETAIL, imageList);
					intent.putExtra(ConstantSet.ONLINE_IMAGE_SMALL, resourceList);
					intent.putExtra("position", position);
					mContext.startActivity(intent);
					mContext.overridePendingTransition(R.anim.activity_enter_scale, R.anim.activity_persistent);
				}
			});
		}
		viewHolder.mTvContent.setText(model.getContent().toString());
		return convertView;
	}

	private Drawable getArrowDrawable(int id) {
		Drawable drawable = mContext.getResources().getDrawable(id);
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}

	class ViewHolder {
		TextView mTvNickName;
		TextView mTvAge;
		MGridView mGvMorePic;
		RoundCornerImageView mIbtnHead;
		ImageView mIvOne;
		TextView mTvContent;
		TextView mTvCommentTime;
		Button mBtnLike;
		ImageView mIvLevel;
		ImageView mIvSex;
		Button mBtnComment;
		ImageView mIvVideo;
		MListView mListView;
		TextView mTvConstellatory;
	}
}
