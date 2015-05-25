package com.xhr88.lp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.util.SharedPreferenceUtil;

public class NewRegisterActivity extends TraineeBaseActivity implements OnClickListener {

	private static final int REGISTER_USER_SUCCESS = 1;
	private static final int REGISTER_USERNEME_FAIL = 2;
	private EditText mEdtUserName;
	private EditText mEdtPassword;
	private LoadingUpView mLoadingUpView;
	private ImageView mIvDisplay;
	private boolean mPasswordVisible;
	private CheckBox mCheckBox;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissLoadingUpView(mLoadingUpView);
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case REGISTER_USER_SUCCESS:
					toast("注册成功");
					startActivity(new Intent(NewRegisterActivity.this, Registerstep1Activity.class));
					sendBroadcast(new Intent(ConstantSet.ACTION_REGISTER_SUCCESS));
					finish();
					break;
				case REGISTER_USERNEME_FAIL:
					showErrorMsg(result);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_new_register);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this);
	}

	private void initViews() {
		mEdtPassword = (EditText) this.findViewById(R.id.edt_register_pwd);
		mEdtUserName = (EditText) this.findViewById(R.id.edt_register_username);
		mIvDisplay = (ImageView) this.findViewById(R.id.login_pass_iv);
		mCheckBox = (CheckBox) findViewById(R.id.cb_user_agreement);
		initPasswordVisibilityButton(mIvDisplay, mEdtPassword);
	}

	private void checkUser() {
		showLoadingUpView(mLoadingUpView);
		new Thread(new Runnable() {

			@Override
			public void run() {
				final String name = mEdtUserName.getText().toString().trim();
				final String password = mEdtPassword.getText().toString().trim();
				ActionResult result = UserReq.userReg(name, password, "", "", "");
				if (ActionResult.RESULT_CODE_SUCCESS.equals(result.ResultCode)) {
					SharedPreferenceUtil.saveValue(NewRegisterActivity.this, ConstantSet.KEY_APP_CONFIG_FILE,
							ConstantSet.KEY_USER_NAME, name);
					sendHandler(REGISTER_USER_SUCCESS, result);
				} else {
					sendHandler(REGISTER_USERNEME_FAIL, result);
				}

			}
		}).start();

	}

	protected void initPasswordVisibilityButton(final ImageView passwordVisibilityToggleView,
			final EditText passwordEditText) {
		passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
		passwordVisibilityToggleView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPasswordVisible = !mPasswordVisible;
				if (mPasswordVisible) {
					passwordVisibilityToggleView.setImageResource(R.drawable.password_btn_press);
					passwordEditText.setTransformationMethod(null);
				} else {
					passwordVisibilityToggleView.setImageResource(R.drawable.password_btn_normal);
					passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
				passwordEditText.setSelection(passwordEditText.length());
			}
		});
	}

	private void sendHandler(int what, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_right:
				if (!mCheckBox.isChecked()) {
					toast("请同意用户协议");
					return;
				}
				String name = mEdtUserName.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(name)) {
					toast("请输入用户名");
					return;
				}
				String password = mEdtPassword.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(password)) {
					toast("请输入密码");
					return;
				}
				if (password.length() > 16 || password.length() < 6) {
					toast("密码由6-16个字符组成，区分大小写");
					return;
				}
				checkUser();
				break;
			case R.id.ll_user_agreement:
				startActivity(new Intent(NewRegisterActivity.this, UserAgreementActivity.class));
				break;
			case R.id.title_with_back_title_btn_left:
				finish();
				break;

			default:
				break;
		}

	}

}
