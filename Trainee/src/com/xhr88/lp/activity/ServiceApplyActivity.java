package com.xhr88.lp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.LittleShopReq;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.util.SharedPreferenceUtil;

/**
 * 服务申请界面
 * 
 * @author zou.sq
 */
public class ServiceApplyActivity extends TraineeBaseActivity implements OnClickListener {

	private LoadingUpView mLoadingUpView;
	private EditText mEdtPhoneNum;
	private EditText mEdtQqNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_apply);
		initVariables();
		initViews();
	}

	private void initViews() {
		mEdtPhoneNum = (EditText) findViewById(R.id.tv_phone_number);
		mEdtQqNum = (EditText) findViewById(R.id.tv_qq_number);
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.title_with_back_title_btn_right:
				applyShop();
				break;

			default:
				break;
		}
	}

	private void applyShop() {
		final String phoneNum = mEdtPhoneNum.getText().toString().trim();
		final String qqNum = mEdtQqNum.getText().toString().trim();
		if (StringUtil.isNullOrEmpty(phoneNum)) {
			toast("请输入手机号码");
			return;
		}
		if (StringUtil.isNullOrEmpty(qqNum)) {
			toast("请输入QQ号");
			return;
		}
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				// 更新开通小店状态为申请中
				startActivity(new Intent(ServiceApplyActivity.this, ShopCheckingActivity.class));
				SharedPreferenceUtil.saveValue(XhrApplicationBase.CONTEXT, ServerAPIConstant.KEY_CONFIG_FILENAME,
						ServerAPIConstant.KEY_STORE_STATUS, "1");
				finish();
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return LittleShopReq.applyShop(phoneNum, qqNum);
			}
		});

	}
}
