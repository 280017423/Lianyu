package com.xhr88.lp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.HelpListModel;

/**
 * 帮助详细界面
 * 
 * @author zou.sq
 */
public class HelpDetailActivity extends TraineeBaseActivity implements OnClickListener {

	private TextView mTvHelpTitle;
	private TextView mTvHelpContent;
	private HelpListModel mHelpListModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_detail);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mHelpListModel = (HelpListModel) getIntent().getSerializableExtra(HelpListModel.class.getName());
		if (null == mHelpListModel) {
			finish();
		}
	}

	private void initViews() {
		mTvHelpTitle = (TextView) findViewById(R.id.tv_help_title);
		mTvHelpContent = (TextView) findViewById(R.id.tv_help_content);
		mTvHelpTitle.setText(mHelpListModel.getTitle());
		mTvHelpContent.setText(mHelpListModel.getContent());
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;

			default:
				break;
		}
	}

}
