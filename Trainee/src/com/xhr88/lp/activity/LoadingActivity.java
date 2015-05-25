package com.xhr88.lp.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;

import com.xhr.framework.authentication.BaseLoginProcessor;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.PackageUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.SystemReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.SharedPreferenceUtil;

/**
 * 启动界面
 * 
 * @author zou.sq
 */
public class LoadingActivity extends TraineeBaseActivity {
	private static final int DISPLAY_TIME = 2000;
	private static final String TAG = "LoadingActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		getConfigInfo();
		testJump();
	}

	private void getConfigInfo() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				SystemReq.start();
			}
		}).start();
	}

	private void testJump() {

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isJumpNewerGuiding()) {
					startActivity(new Intent(LoadingActivity.this, NewerGuidingActivity.class));
				} else {
					// 如果已经有用户信息了，跳转到首页，没有用户信息，跳转到登录界面
					if (UserMgr.hasUserInfo()) {
						UserViewModel userViewModel = UserDao.getLocalUserInfo();
						if (!StringUtil.isNullOrEmpty(userViewModel.UserInfo.getNickname())) {
							startActivity(new Intent(LoadingActivity.this, MainActivity.class));
						} else {
							startActivity(new Intent(LoadingActivity.this, Registerstep1Activity.class));
						}
					} else {
						Intent intent = new Intent(mContext, RegisterActivity.class);
						intent.putExtra(BaseLoginProcessor.KEY_LOGIN_TYPE, ConstantSet.EXIT_TO_CANCEL_APK);
						startActivity(intent);
					}
				}
				finish();
			}
		}, DISPLAY_TIME);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean isJumpNewerGuiding() {

		String versionString = SharedPreferenceUtil.getStringValueByKey(LoadingActivity.this,
				ConstantSet.KEY_NEWER_GUIDING_FILE, ConstantSet.KEY_NEWER_GUIDING_FINISH);

		int code = -1;
		try {
			code = PackageUtil.getVersionCode();
		} catch (NameNotFoundException e) {
			EvtLog.w(TAG, e);
		}
		if (!StringUtil.isNullOrEmpty(versionString) && versionString.equals(code + "")) {
			return false;
		}
		return true;
	}

}
