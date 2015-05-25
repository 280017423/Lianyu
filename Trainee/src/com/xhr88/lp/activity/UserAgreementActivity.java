package com.xhr88.lp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.xhr88.lp.R;

/**
 * 用户协议界面
 * 
 * @author zou.sq
 */
public class UserAgreementActivity extends TraineeBaseActivity implements OnClickListener {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_agreement);
		initViews();
	}

	private void initViews() {
		mWebView = (WebView) findViewById(R.id.wv_agreement_content);
		mWebView.loadUrl("file:///android_asset/user_agreement.html");
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
