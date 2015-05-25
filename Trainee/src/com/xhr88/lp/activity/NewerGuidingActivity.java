package com.xhr88.lp.activity;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;
import com.xhr.framework.authentication.BaseLoginProcessor;
import com.xhr.framework.util.HttpClientUtil;
import com.xhr.framework.util.PackageUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.NewerAdapter;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.SharedPreferenceUtil;
import com.xhr88.lp.widget.AutoScrollViewPager;

/**
 * 新手引导界面
 * 
 * @author zou.sq
 */
public class NewerGuidingActivity extends TraineeBaseActivity {

	private int mCurrentPosition;
	private int mCurrentPageScrollStatus;
	private AutoScrollViewPager mViewPager;
	private int[] mImages = new int[] {
			R.drawable.newer_guiding_1,
			R.drawable.newer_guiding_2,
			R.drawable.newer_guiding_3 };
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newer_guiding);
		initVariables();
		findView();
	}

	private void findView() {
		mViewPager = (AutoScrollViewPager) findViewById(R.id.vp_newer_guide);
		mViewPager.setAdapter(new NewerAdapter(this, mImages));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mCurrentPosition = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if (position == mImages.length - 1 && positionOffsetPixels == 0 && mCurrentPageScrollStatus == 1) {
					saveNewerGuidingStatus();
					jumpToMain();
				}
			}

			@Override
			public void onPageScrollStateChanged(int status) {
				mCurrentPageScrollStatus = status;
			}
		});
	}

	private void initVariables() {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCurrentPosition <= 0) {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					toast("再按一次退出程序");
					mExitTime = System.currentTimeMillis();
				} else {
					MobclickAgent.onKillProcess(this);
					HttpClientUtil.setCookieStore(null);
					finish();
				}
			} else {
				mViewPager.setCurrentItem(mCurrentPosition - 1, true);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void saveNewerGuidingStatus() {
		new Thread() {
			@Override
			public void run() {
				int code = -1;
				try {
					code = PackageUtil.getVersionCode();
				} catch (NameNotFoundException e) {
				}
				SharedPreferenceUtil.saveValue(mContext, ConstantSet.KEY_NEWER_GUIDING_FILE,
						ConstantSet.KEY_NEWER_GUIDING_FINISH, code + "");
			}
		}.start();
	}

	private void jumpToMain() {
		if (UserMgr.hasUserInfo()) {
			UserViewModel userViewModel = UserDao.getLocalUserInfo();
			if (!StringUtil.isNullOrEmpty(userViewModel.UserInfo.getNickname())) {
				startActivity(new Intent(this, MainActivity.class));
			} else {
				startActivity(new Intent(this, Registerstep1Activity.class));
			}
		} else {
			Intent intent = new Intent(mContext, RegisterActivity.class);
			intent.putExtra(BaseLoginProcessor.KEY_LOGIN_TYPE, ConstantSet.EXIT_TO_CANCEL_APK);
			startActivity(intent);
		}
		finish();
	}
}
