package com.xhr88.lp.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.xhr.framework.util.ImeUtil;
import com.xhr.framework.util.NetUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.listener.IActionListener;

/**
 * 修改密码界面
 * 
 * @author zou.sq
 */
public class ChangePwdActivity extends TraineeBaseActivity implements OnClickListener {

	private EditText mEdtOriginPwd;
	private EditText mEdtNewPwd;
	private EditText mEdtNewPwdAgain;
	private boolean mIsUpdating;
	private LoadingUpView mLoadingUpView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pwd);
		initVariable();
		initViews();
	}

	private void initVariable() {
		mLoadingUpView = new LoadingUpView(this);
	}

	private void initViews() {
		mEdtOriginPwd = (EditText) findViewById(R.id.edt_origin_pwd);
		mEdtNewPwd = (EditText) findViewById(R.id.edt_new_pwd);
		mEdtNewPwdAgain = (EditText) findViewById(R.id.edt_new_pwd_again);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.title_with_back_title_btn_right:
				checkAndSubmit();
				break;

			default:
				break;
		}
	}

	private void checkAndSubmit() {
		if (mIsUpdating) {
			return;
		}
		ImeUtil.hideSoftInput(this);
		final String originPwd = mEdtOriginPwd.getText().toString().trim();
		final String newPwd = mEdtNewPwd.getText().toString().trim();
		String newPwdAgain = mEdtNewPwdAgain.getText().toString().trim();
		if (StringUtil.isNullOrEmpty(originPwd)) {
			toast("请输入当前密码");
			return;
		}
		if (StringUtil.isNullOrEmpty(newPwd)) {
			toast("请输入新密码");
			return;
		}
		if (newPwd.length() < 6) {
			toast("密码由6-16个字符组成，区分大小写");
			return;
		}
		if (StringUtil.isNullOrEmpty(newPwdAgain)) {
			toast("请重复输入新密码");
			return;
		}
		if (newPwdAgain.length() < 6) {
			toast("密码由6-16个字符组成，区分大小写");
			return;
		}
		if (!newPwd.equals(newPwdAgain)) {
			toast("新密码2次输入不同，请检查");
			return;
		}
		if (!NetUtil.isNetworkAvailable()) {
			toast(getString(R.string.network_is_not_available));
			return;
		}
		mIsUpdating = true;
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(ChangePwdActivity.this, true, false, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				mIsUpdating = false;
				toast("密码修改成功");
				NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				nm.cancelAll();
				UserMgr.logout();
				startActivity(new Intent(ChangePwdActivity.this, RegisterActivity.class));
				sendBroadcast(new Intent(ACTION_EXIT_APP));
				finish();
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				mIsUpdating = false;
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.changePwd(originPwd, newPwd);
			}
		});
	}
}
