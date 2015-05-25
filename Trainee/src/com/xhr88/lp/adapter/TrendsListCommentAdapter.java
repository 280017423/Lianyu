package com.xhr88.lp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.MainActivity;
import com.xhr88.lp.activity.OtherHomeActivity;
import com.xhr88.lp.activity.MainActivity.TabHomeIndex;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.model.datamodel.CommentModel;

public class TrendsListCommentAdapter extends BaseAdapter {

	private List<CommentModel> mDynamicListModels;
	private Activity mContext;

	public TrendsListCommentAdapter(Activity activity, List<CommentModel> dataModel) {
		this.mContext = activity;
		this.mDynamicListModels = dataModel;
	}

	@Override
	public int getCount() {
		if (mDynamicListModels != null && !mDynamicListModels.isEmpty()) {
			return mDynamicListModels.size();
		}
		return 0;
	}

	@Override
	public CommentModel getItem(int position) {
		if (mDynamicListModels != null && !mDynamicListModels.isEmpty()) {
			return mDynamicListModels.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<CommentModel> getCommentList() {
		return mDynamicListModels;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.view_my_comment_list_item, null);

			viewHolder.mTvContent = (TextView) convertView.findViewById(R.id.tv_comment_content);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final CommentModel model = getItem(position);

		ArrayList<String> contents = new ArrayList<String>();
		contents.add(model.getNickname());
		if (!StringUtil.isNullOrEmpty(model.getComnickname())) {
			contents.add(" 回复  ");
			contents.add(model.getComnickname());
		}
		contents.add(" : " + model.getContent());
		viewHolder.mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
		viewHolder.mTvContent.setText(addClickablePart(contents, model), BufferType.SPANNABLE);
		return convertView;
	}

	class ViewHolder {
		TextView mTvContent;
	}

	private SpannableStringBuilder addClickablePart(ArrayList<String> contents, final CommentModel model) {
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
			final int index = i;
			if (i % 2 == 0) {
				ssb.setSpan(new ClickableSpan() {

					@Override
					public void onClick(View widget) {
						String uid = "";
						if (0 == index) {
							uid = model.getUid();
						} else {
							uid = model.getComuid();
						}
						if (UserMgr.isMineUid(uid)) {
							MainActivity.INSTANCE.setTabSelection(TabHomeIndex.HOME_MY_CENTER, false);
							Intent intent = new Intent(mContext, MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							mContext.startActivity(intent);
						} else {
							Intent intent = new Intent(mContext, OtherHomeActivity.class);
							intent.putExtra("touid", uid);
							mContext.startActivity(intent);
						}
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
}
