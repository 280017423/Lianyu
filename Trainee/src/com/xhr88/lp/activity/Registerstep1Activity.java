package com.xhr88.lp.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;

public class Registerstep1Activity extends TraineeBaseActivity implements OnClickListener {

	private static final int DIALOG_NEXT_STEP = 1;
	private static final int CHECK_USERNEME_SUCCESS = 1;
	private static final int CHECK_USERNEME_FAIL = 2;
	private ImageView mIvManCheck;
	private ImageView mIvWomanCheck;
	private EditText mEdtNickName;
	private String mSexValue;
	private ImageView mIvDelete;
	private long mExitTime;
	private LoadingUpView mLoadingUpView;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissLoadingUpView(mLoadingUpView);
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case CHECK_USERNEME_SUCCESS:
					if (!"0".equals(result.ResultObject)) {
						String nickName = mEdtNickName.getText().toString().trim();
						Intent intent = new Intent(Registerstep1Activity.this, Registerstep2Activity.class);
						intent.putExtra("nickName", StringUtil.replaceBlank(nickName));
						intent.putExtra("sex", mSexValue);
						startActivity(intent);
					} else {
						toast("该昵称已被注册");
					}
					break;
				case CHECK_USERNEME_FAIL:
					showErrorMsg(result);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntentFilter.addAction(ConstantSet.ACTION_LOGIN_SUCCESS);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_register_step1);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this);
	}

	private void initViews() {
		mIvDelete = (ImageView) findViewById(R.id.iv_delete);
		mEdtNickName = (EditText) findViewById(R.id.edt_user_nickname);
		mIvManCheck = (ImageView) findViewById(R.id.imageView1);
		mIvWomanCheck = (ImageView) findViewById(R.id.imageView2);
		mEdtNickName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().trim().length() > 0) {
					UIUtil.setViewVisible(mIvDelete);
				} else {
					UIUtil.setViewGone(mIvDelete);
				}
			}
		});
		String nickName = getIntent().getStringExtra("nickName");
		if (!StringUtil.isNullOrEmpty(nickName)) {
			mEdtNickName.setText(nickName);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.title_with_back_title_btn_right:
				checkAndNext();
				break;
			case R.id.iv_delete:
				mEdtNickName.setText("");
				break;
			case R.id.layout_man:
				mIvWomanCheck.setVisibility(View.GONE);
				mIvManCheck.setVisibility(View.VISIBLE);
				mSexValue = "1";
				break;
			case R.id.layout_woman:
				mIvManCheck.setVisibility(View.GONE);
				mIvWomanCheck.setVisibility(View.VISIBLE);
				mSexValue = "2";
				break;
			default:
				break;
		}
	}

	private void checkAndNext() {
		String nickName = mEdtNickName.getText().toString().trim();
		if (StringUtil.isNullOrEmpty(nickName)) {
			toast("请输入昵称");
			return;
		}
		if (StringUtil.isNullOrEmpty(mSexValue)) {
			toast("请选择性别");
			return;
		}
		showDialog(DIALOG_NEXT_STEP);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_NEXT_STEP:
				return createDialogBuilder(this, getString(R.string.button_text_tips), "性别设置后将不可以更改",
						getString(R.string.button_text_no), "确定").create(id);
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_NEXT_STEP:
				checkNickname();
				break;
			default:
				break;
		}
	}

	private void checkNickname() {
		final String nickname = mEdtNickName.getText().toString().trim();
		showLoadingUpView(mLoadingUpView);
		new Thread(new Runnable() {

			@Override
			public void run() {
				ActionResult result = UserReq.checknickname(nickname);
				if (ActionResult.RESULT_CODE_SUCCESS.equals(result.ResultCode)) {
					sendHandler(CHECK_USERNEME_SUCCESS, result);
				} else {
					sendHandler(CHECK_USERNEME_FAIL, result);
				}
			}
		}).start();
	}

	private void sendHandler(int what, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				toast("再按一次退出程序");
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void processBroadReceiver(String action, Object data) {
		super.processBroadReceiver(action, data);
		if (action.equals(ConstantSet.ACTION_LOGIN_SUCCESS)) {
			finish();
		}
	}

}
