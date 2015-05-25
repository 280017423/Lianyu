package com.xhr88.lp.activity;

import java.util.List;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xhr.framework.authentication.BaseLoginProcessor;
import com.xhr.framework.util.PackageUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.business.manager.SystemSettingMgr;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.SystemReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.ConfigModel;
import com.xhr88.lp.model.datamodel.UpdateModel;
import com.xhr88.lp.util.PopWindowUtil;

/**
 * 设置界面
 * 
 * @author zou.sq
 */
public class SettingActivity extends TraineeBaseActivity implements OnClickListener {

	private static final int DIALOG_LOGIN_OUT = 1;
	private LoadingUpView mLoadingUpView;
	private TextView mTvCacheSize;
	private TextView mTvVersionContent;
	private TextView mTvVersioNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntentFilter.addAction(ACTION_EXIT_APP);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initVariables();
		initViews();
		calcCache();
		initVersion();
		checkVersion(false);
	}

	private void checkVersion(final boolean isShow) {
		if (isShow) {
			showLoadingUpView(mLoadingUpView);
		}
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				if (isShow) {
					dismissLoadingUpView(mLoadingUpView);
				}
				UpdateModel model = (UpdateModel) result.ResultObject;
				if (null != model) {
					if (1 == model.getIsupdate()) {
						mTvVersionContent.setText(getString(R.string.new_version, model.getUpdatever()));
						UIUtil.setViewVisible(mTvVersioNew);
						if (isShow) {
							Intent intent = new Intent(SettingActivity.this, UpdateActivity.class);
							intent.putExtra(UpdateModel.class.getName(), model);
							startActivity(intent);
						}
					} else {
						UIUtil.setViewGone(mTvVersioNew);
						try {
							mTvVersionContent.setText(getString(R.string.current_version, PackageUtil.getVersionName()));
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}
						if (isShow) {
							toast("已经是最新版本");
						}
					}
				}
			}

			@Override
			public void onError(ActionResult result) {
				if (isShow) {
					dismissLoadingUpView(mLoadingUpView);
					showErrorMsg(result);
				}
			}

			@Override
			public ActionResult onAsyncRun() {
				return SystemReq.checkUpdate();
			}
		});

	}

	private void initVersion() {
		List<ConfigModel> models = DBMgr.getHistoryData(ConfigModel.class);
		if (null != models && !models.isEmpty()) {
			ConfigModel model = models.get(0);
			if (1 == model.getIsupdate()) {
				mTvVersionContent.setText(getString(R.string.new_version, model.getUpdatever()));
				UIUtil.setViewVisible(mTvVersioNew);
			} else {
				UIUtil.setViewGone(mTvVersioNew);
				try {
					mTvVersionContent.setText(getString(R.string.current_version, PackageUtil.getVersionName()));
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this);
	}

	private void initViews() {
		mTvCacheSize = (TextView) findViewById(R.id.tv_cache_size);
		mTvVersioNew = (TextView) findViewById(R.id.tv_version_new);
		mTvVersionContent = (TextView) findViewById(R.id.tv_version_content);
		mTvCacheSize.setText("计算中...");
		// 登录类型 0=本站 1=微信,2=QQ,3=weibo
		// 如果不是本站登录，则隐藏修改密码选项
		View loginView = findViewById(R.id.rl_change_pwd_item);
		if (UserMgr.isXhrLogin()) {
			UIUtil.setViewVisible(loginView);
		} else {
			UIUtil.setViewGone(loginView);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.rl_change_pwd_item:
				startActivity(new Intent(SettingActivity.this, ChangePwdActivity.class));
				break;
			case R.id.rl_msg_toast_item:
				toast("正在开发中...");
				break;
			case R.id.rl_clean_cache_item:
				showCleanPop();
				break;
			case R.id.rl_feedback_item:
				startActivity(new Intent(SettingActivity.this, FeedbackActivity.class));
				break;
			case R.id.rl_version_check_item:
				checkVersion(true);
				break;
			case R.id.rl_user_agreement:
				startActivity(new Intent(SettingActivity.this, UserAgreementActivity.class));
				break;
			case R.id.btn_login_out:
				showDialog(DIALOG_LOGIN_OUT);
				break;

			default:
				break;
		}
	}

	private void showCleanPop() {
		final View popView = View.inflate(this, R.layout.view_clean_cache, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);
		final Button btnClean = (Button) popView.findViewById(R.id.btn_clean_cache);
		final Button btnCancel = (Button) popView.findViewById(R.id.btn_clean_cache_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
			}
		});
		btnClean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				clearCache();
			}
		});
		popManager.show(Gravity.BOTTOM);
	}

	@Override
	protected void processBroadReceiver(String action, Object data) {
		if (ACTION_EXIT_APP.equals(action) && !isFinishing()) {
			finish();
			return;
		}
		super.processBroadReceiver(action, data);
	}

	private void clearCache() {
		new GetCacheSizeTask().execute(true);
	}

	private void calcCache() {
		new GetCacheSizeTask().execute(false);
	}

	/**
	 * 传入参数表示是否需要清空缓存
	 */
	class GetCacheSizeTask extends AsyncTask<Boolean, Void, String> {
		boolean mNeedsToClearCache;

		@Override
		protected String doInBackground(Boolean... params) {
			if (params != null && params[0]) {
				mNeedsToClearCache = true;
				publishProgress();
				SystemSettingMgr.deleteFiles(SettingActivity.this);
			}
			return SystemSettingMgr.getCacheFileSize(SettingActivity.this);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			showLoadingUpView(mLoadingUpView);
		}

		@Override
		protected void onPostExecute(String result) {
			mTvCacheSize.setText(result);
			if (mNeedsToClearCache) {
				dismissLoadingUpView(mLoadingUpView);
				toast("缓存清理完毕");
			}
		}
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if (DIALOG_LOGIN_OUT == id) {
			return createDialogBuilder(this, getString(R.string.button_text_tips), "确定退出当前账号吗？",
					getString(R.string.button_text_no), "确定").create(id);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_LOGIN_OUT:
				NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				nm.cancelAll();
				UserMgr.logout();
				Intent intent = new Intent(SettingActivity.this, RegisterActivity.class);
				intent.putExtra(BaseLoginProcessor.KEY_LOGIN_TYPE, ConstantSet.EXIT_TO_CANCEL_APK);
				startActivity(intent);
				sendBroadcast(new Intent(ACTION_EXIT_APP));
				finish();
				break;
			default:
				break;
		}
	}
}
