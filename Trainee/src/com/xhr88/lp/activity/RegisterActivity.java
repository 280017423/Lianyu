package com.xhr88.lp.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.umeng.analytics.MobclickAgent;
import com.xhr.framework.authentication.BaseLoginProcessor;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.HttpClientUtil;
import com.xhr.framework.util.NetUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.viewmodel.UserViewModel;

public class RegisterActivity extends TraineeBaseActivity implements OnClickListener {

	private final int LOGIN_SUCCESS = 1;
	private final int LOGIN_ERROR = 2;
	private static final int MSG_AUTH_COMPLETE = 4;
	private static final int MSG_AUTH_ERROR = 5;
	private static final int MSG_AUTH_CANCEL = 6;
	private static final String TAG = "RegisterActivity";
	private LoadingUpView mLoadingUpView;
	private String mOpenId;
	private Long mExpiresIn;
	private String mAccessToken;
	private String mUserName;
	private int mLoginType;
	private String mLogingJumpType;
	private View mIvTopBg;
	private int mScreenWidth;
	private int mScreenHeight;
	private View mViewBottom;
	private View mViewTop;
	private long mExitTime;

	@Override
	protected void processBroadReceiver(String action, Object data) {
		super.processBroadReceiver(action, data);
		if (action.equals(ConstantSet.ACTION_REGISTER_SUCCESS)) {
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntentFilter.addAction(ConstantSet.ACTION_REGISTER_SUCCESS);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_register);
		initVariables();
		initViews();
	}

	private void initVariables() {
		ShareSDK.initSDK(this);
		mScreenWidth = UIUtil.getScreenWidth(this);
		mScreenHeight = UIUtil.getScreenHeight(this);
		mLoadingUpView = new LoadingUpView(this, true);
		mLogingJumpType = getIntent().getStringExtra(BaseLoginProcessor.KEY_LOGIN_TYPE);
	}

	private void initViews() {
		mIvTopBg = findViewById(R.id.iv_top_bg);
		mViewTop = findViewById(R.id.layout_top);
		mViewBottom = findViewById(R.id.layout_bottom);
		UIUtil.setViewHeight(mViewBottom, mScreenHeight / 3);
		mViewTop.setMinimumHeight(mScreenHeight * 2 / 3);
		calculatView();
	}

	private void calculatView() {
		int h = (int) (0.9 * mScreenWidth);
		UIUtil.setViewHeight(mIvTopBg, h);
	}

	@Override
	public void onResume() {
		super.onResume();
		mLoadingUpView.onResume();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case LOGIN_SUCCESS:
					dismissLoadingUpView(mLoadingUpView);
					ActionResult result = (ActionResult) msg.obj;
					UserViewModel userInfo = (UserViewModel) result.ResultObject;
					if (!StringUtil.isNullOrEmpty(userInfo.UserInfo.getNickname())) {
						Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(RegisterActivity.this, Registerstep1Activity.class);
						intent.putExtra("nickName", mUserName);
						startActivity(intent);
					}
					finish();
					break;
				case LOGIN_ERROR:
					ActionResult result1 = (ActionResult) msg.obj;
					dismissLoadingUpView(mLoadingUpView);
					showErrorMsg(result1);
					break;
				case MSG_AUTH_COMPLETE:
					if (!isFinishing()) {
						showLoadingUpView(mLoadingUpView);
						if (null != mUserName && mUserName.length() > 8) {
							mUserName = mUserName.substring(0, 8);
						}
						login(mLoginType, mUserName, mOpenId, mAccessToken, mExpiresIn + "");
					}
					break;
				case MSG_AUTH_CANCEL:
					toast("取消授权");
					dismissLoadingUpView(mLoadingUpView);
					break;
				case MSG_AUTH_ERROR:
					dismissLoadingUpView(mLoadingUpView);
					toast((String) msg.obj);
					break;
			}
		}
	};

	private void login(final int type, final String userName, final String openId, final String accesstoken,
			final String expiredate) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ActionResult result = UserReq.userLogin(type, userName, "", openId, accesstoken, expiredate);
				if (ActionResult.RESULT_CODE_SUCCESS.equals(result.ResultCode)) {
					sendHandler(LOGIN_SUCCESS, result);
				} else {
					sendHandler(LOGIN_ERROR, result);
				}
			}
		}).start();
	}

	private void sendHandler(int what, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	private void authorize(Platform plat) {
		if (null == plat) {
			return;
		}
		EvtLog.d(TAG, "authorize:" + plat.getName());
		if (plat.isValid()) {
			plat.removeAccount();
		}
		plat.setPlatformActionListener(new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int action, Throwable throwable) {
				Message msg = mHandler.obtainMessage(MSG_AUTH_ERROR);
				EvtLog.d(TAG, "授权失败：" + throwable.getMessage());
				msg.obj = throwable.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
				mOpenId = platform.getDb().getUserId();
				mExpiresIn = platform.getDb().getExpiresIn();
				mAccessToken = platform.getDb().getToken();
				mUserName = platform.getDb().getUserName();
				EvtLog.d(TAG, "授权成功：mOpenId=" + mOpenId);
				if (!StringUtil.isNullOrEmpty(mOpenId)) {
					if (action == Platform.ACTION_USER_INFOR) {
						Message msg = mHandler.obtainMessage(MSG_AUTH_COMPLETE);
						msg.obj = new Object[] { platform.getName(), res };
						mHandler.sendMessage(msg);
					}
				}
			}

			@Override
			public void onCancel(Platform platform, int action) {
				EvtLog.d(TAG, "取消授权");
				Message msg = mHandler.obtainMessage(MSG_AUTH_CANCEL);
				mHandler.sendMessage(msg);
			}
		});
		plat.SSOSetting(false);
		plat.showUser(null);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.ll_weixin_login:
				if (!NetUtil.isNetworkAvailable()) {
					toast(getString(R.string.network_is_not_available));
					return;
				}
				mLoginType = 1;
				authorize(ShareSDK.getPlatform(Wechat.NAME));
				break;
			case R.id.ll_qq_login:
				if (!NetUtil.isNetworkAvailable()) {
					toast(getString(R.string.network_is_not_available));
					return;
				}
				mLoginType = 2;
				authorize(ShareSDK.getPlatform(QQ.NAME));
				break;
			case R.id.ll_sina_login:

				if (!NetUtil.isNetworkAvailable()) {
					toast(getString(R.string.network_is_not_available));
					return;
				}
				mLoginType = 3;
				authorize(ShareSDK.getPlatform(SinaWeibo.NAME));
				break;
			case R.id.btn_register_login_btn:
				intent = new Intent(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mLoadingUpView.isShowing()) {
				dismissLoadingUpView(mLoadingUpView);
			}
			if (mLogingJumpType != null && mLogingJumpType.equals(ConstantSet.EXIT_TO_CANCEL_APK)) {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					toast("再按一次退出程序");
					mExitTime = System.currentTimeMillis();
				} else {
					MobclickAgent.onKillProcess(this);
					HttpClientUtil.setCookieStore(null);
					finish();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
