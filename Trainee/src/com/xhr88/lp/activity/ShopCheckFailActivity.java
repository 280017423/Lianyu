package com.xhr88.lp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.util.UIUtil;
import com.xhr88.lp.R;

/**
 * 小店审核中界面
 * 
 * @author zou.sq
 */
public class ShopCheckFailActivity extends TraineeBaseActivity implements OnClickListener {

	private TextView mTvFailReason;
	private ImageView mIvBg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_check_fail);
		initViews();
	}

	private void initViews() {
		mTvFailReason = (TextView) findViewById(R.id.tv_fail_reason);
		mIvBg = (ImageView) findViewById(R.id.iv_reapply_bg);
		UIUtil.setViewHeight(mIvBg, UIUtil.getScreenWidth(this) * 480 / 720);
		String failInfo = getIntent().getStringExtra("failInfo");
		mTvFailReason.setText(failInfo);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.btn_reapply:
				startActivity(new Intent(ShopCheckFailActivity.this, ServiceApplyActivity.class));
				finish();
				break;

			default:
				break;
		}
	}

}
