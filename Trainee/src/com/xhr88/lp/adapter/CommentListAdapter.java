package com.xhr88.lp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.CommentModel;
import com.xhr88.lp.util.DateUtil;

/**
 * 动态详情里面的评论列表
 * 
 * @author zou.sq
 */
public class CommentListAdapter extends BaseAdapter {

	private List<CommentModel> mCommentModels;
	private Context mContext;
	private ImageLoader mImageLoader;
	private OnClickListener mOnClickListener;

	public CommentListAdapter(Activity activity, List<CommentModel> dataList, ImageLoader loader,
			OnClickListener listener) {
		this.mContext = activity;
		this.mCommentModels = dataList;
		this.mImageLoader = loader;
		mOnClickListener = listener;
	}

	@Override
	public int getCount() {
		if (mCommentModels != null && !mCommentModels.isEmpty()) {
			return mCommentModels.size();
		}
		return 0;

	}

	@Override
	public CommentModel getItem(int position) {
		if (mCommentModels != null) {
			return mCommentModels.get(position);
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
			convertView = View.inflate(mContext, R.layout.view_comment_item, null);
			holder.mIvHeadImg = (RoundCornerImageView) convertView.findViewById(R.id.ibtn_head_img);
			holder.mTvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
			holder.mTvDate = (TextView) convertView.findViewById(R.id.tv_date);
			holder.mTvContent = (TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final CommentModel model = getItem(position);
		int imageRes = R.drawable.default_man_head_bg;
		if (1 != model.getSex()) {
			imageRes = R.drawable.default_woman_head_bg;
		}
		mImageLoader.displayImage(model.getHeadimg(), holder.mIvHeadImg, new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageRes)
				.build());
		holder.mIvHeadImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				view.setTag(model.getUid());
				mOnClickListener.onClick(view);
			}
		});
		holder.mTvNickName.setText(model.getNickname());
		try {
			holder.mTvDate.setText(DateUtil.getCommentTime(model.getCreatetime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int type = model.getType();
		if (type == 2) {
			ArrayList<String> contents = new ArrayList<String>();
			contents.add(" 回复  ");
			contents.add(model.getComnickname());
			contents.add(" : " + model.getContent());
			holder.mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
			holder.mTvContent.setText(addClickablePart(contents, model.getComuid()), BufferType.SPANNABLE);
		} else if (type == 1) {
			holder.mTvContent.setText(model.getContent());
		}
		return convertView;
	}

	private SpannableStringBuilder addClickablePart(ArrayList<String> contents, final String touid) {
		if (null == contents || contents.isEmpty()) {
			return new SpannableStringBuilder();
		}
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		for (int i = 0; i < contents.size(); i++) {
			ssb.append(contents.get(i));
		}
		for (int i = 0; i < contents.size(); i++) {
			final String name = contents.get(i);
			final int start = ssb.toString().indexOf(name);
			if (i % 2 == 1) {
				ssb.setSpan(new ClickableSpan() {

					@Override
					public void onClick(View view) {
						view.setTag(touid);
						mOnClickListener.onClick(view);
					}

					@Override
					public void updateDrawState(TextPaint ds) {
						super.updateDrawState(ds);
						ds.setColor(Color.parseColor("#b593d6")); // 设置文本颜色
						ds.setUnderlineText(false); // 去掉下划线
					}
				}, start, start + name.length(), 0);
			}
		}
		return ssb;
	}

	class viewHode {
		RoundCornerImageView mIvHeadImg;
		TextView mTvNickName;
		TextView mTvDate;
		TextView mTvContent;
	}
}
