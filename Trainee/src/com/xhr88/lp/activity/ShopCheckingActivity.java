package com.xhr88.lp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.xhr88.lp.R;

/**
 * 小店审核中界面
 * 
 * @author zou.sq
 */
public class ShopCheckingActivity extends TraineeBaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_checking);
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
