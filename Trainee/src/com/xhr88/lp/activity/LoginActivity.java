package com.xhr88.lp.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.util.ImeUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.SharedPreferenceUtil;

public class LoginActivity extends TraineeBaseActivity implements OnClickListener {

	public static final int LOGIN_USER_SUCCESS = 1;
	public static final int LOGIN_USERNEME_FAIL = 2;
	private EditText mEdtUsername;
	private EditText mEdtPwd;
	private LoadingUpView mLoadingUpView;
	private ImageView mIvDisplayPwd;
	private boolean mPasswordVisible;
	private String mExistUserName;
	private TextView mTvRegister;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissLoadingUpView(mLoadingUpView);
			switch (msg.what) {
				case LOGIN_USER_SUCCESS:
					ActionResult result = (ActionResult) msg.obj;
					UserViewModel userInfo = (UserViewModel) result.ResultObject;
					if (!StringUtil.isNullOrEmpty(userInfo.UserInfo.getNickname())) {
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(LoginActivity.this, Registerstep1Activity.class);
						startActivity(intent);
					}
					sendBroadcast(new Intent(ConstantSet.ACTION_REGISTER_SUCCESS));
					finish();
					break;
				case LOGIN_USERNEME_FAIL:
					showErrorMsg((ActionResult) msg.obj);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntentFilter.addAction(ConstantSet.ACTION_REGISTER_SUCCESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this, true);
		mExistUserName = SharedPreferenceUtil.getStringValueByKey(this, ConstantSet.KEY_APP_CONFIG_FILE,
				ConstantSet.KEY_USER_NAME);
	}

	private void initViews() {
		mIvDisplayPwd = (ImageView) findViewById(R.id.iv_login_display_pwd);
		mEdtUsername = (EditText) findViewById(R.id.edt_login_username);
		mEdtPwd = (EditText) findViewById(R.id.edt_login_password);
		mTvRegister = (TextView) findViewById(R.id.tv_newer_register);
		mTvRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		mTvRegister.getPaint().setAntiAlias(true);// 抗锯齿
		if (!StringUtil.isNullOrEmpty(mExistUserName)) {
			mEdtUsername.setText(mExistUserName);
		}
		initPasswordVisibilityButton(mIvDisplayPwd, mEdtPwd);
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
		Message msg = new Message();
		msg.what = what;
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	@Override
	protected void processBroadReceiver(String action, Object data) {
		super.processBroadReceiver(action, data);
		if (action.equals(ConstantSet.ACTION_REGISTER_SUCCESS)) {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_right:
				ImeUtil.hideSoftInput(LoginActivity.this, mEdtUsername);
				final String tempUsername = mEdtUsername.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(tempUsername)) {
					toast("请输入用户名");
					return;
				}
				Pattern pt = Pattern.compile("^[0-9a-zA-Z]+$");
				Matcher mt = pt.matcher(tempUsername);
				if (!mt.matches()) {
					toast("请检查用户名格式，由数字或英文字母组成，15个字符以内。");
					return;
				}
				final String temppassword = mEdtPwd.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(temppassword)) {
					toast("请输入密码");
					return;
				}
				if (temppassword.length() < 6) {
					toast("密码长度最小为6");
					return;
				}
				showLoadingUpView(mLoadingUpView);
				new Thread(new Runnable() {

					@Override
					public void run() {
						ActionResult result = UserReq.userLogin(0, tempUsername, temppassword, "", "", "");
						if (ActionResult.RESULT_CODE_SUCCESS.equals(result.ResultCode)) {
							SharedPreferenceUtil.saveValue(LoginActivity.this, ConstantSet.KEY_APP_CONFIG_FILE,
									ConstantSet.KEY_USER_NAME, tempUsername);
							sendHandler(LOGIN_USER_SUCCESS, result);
						} else {
							sendHandler(LOGIN_USERNEME_FAIL, result);
						}
					}
				}).start();
				break;
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.tv_newer_register:
				Intent intent = new Intent(LoginActivity.this, NewRegisterActivity.class);
				LoginActivity.this.startActivity(intent);
				break;

		}
	}

}
