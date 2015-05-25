package com.xhr88.lp.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xhr88.lp.R;

public class ViewUtil {

	public static View getEmptyView(Context context, String info) {
		View emptyView = View.inflate(context, R.layout.view_empty_layout, null);
		TextView emptyInfo = (TextView) emptyView.findViewById(R.id.tv_empty_info);
		emptyInfo.setText(info);
		return emptyView;
	}
}
