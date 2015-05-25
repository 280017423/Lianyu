package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xhr.framework.app.XhrActivityBase.ActionListener;
import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.PackageUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IDialogProtocol;
import com.xhr88.lp.util.DialogManager;
import com.xhr88.lp.widget.CustomDialog.Builder;

/**
 * @author zou.sq
 */
public class FragmentBaseActivity extends FragmentActivity implements IDialogProtocol {

	public static final String TAG = "ActivityBase";
	public static final String ACTION_EXIT_APP = "EXIT_APP";
	public static final String ACTION_RESET_APP = "ACTION_RESET_APP";
	public static final String KEY_WORD_APP = "KEY_WORD_APP";
	public static final String ACTION_DEFAULT_BROAD = "TestCustomBroadCast";

	public static final String ACTION_LOGIN_SUCCESS = "LoginBroadCast";

	private static final int DEFAUTL_COOLING_TIME = 3000;
	private static final String DEFAULT_ERROR_MSG = "error";
	private static final String DEFAULT_NULL_MSG = "[]";
	private static final List<String> ACTION_LIST = new ArrayList<String>();

	private static final Object mSync = new Object();
	public static Context CONTEXT;
	private Toast mToast;

	private boolean mIsUseBroadcase = true;
	// 是否需要刷新数据（比如跳转到其它界面操作之后返回）
	private boolean mIsRefreshData = false;
	protected final IntentFilter mIntentFilter = new IntentFilter();
	protected BroadcastReceiver mGlobalReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Object data = intent.getSerializableExtra("data");
			processBroadReceiver(action, data);
		}
	};

	@Override
	protected void onRestart() {
		super.onRestart();
		EvtLog.d(TAG, "onRestart");
		if (isRefreshData()) {
			// 非第一次进来刷新；
			refreshData();
			return;
		}
	}

	/**
	 * 刷新数据，子类实现数据刷新
	 */
	protected void refreshData() {
		// 刷新之后 ，只有下次操作返回之后重新设置刷新标记
		setmIsRefreshData(false);
	}

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		// MobclickAgent.openActivityDurationTrack(false);
		if (ismIsUseBroadcase()) {
			// 外部控制是否需要启用广播,默认为否
			addGlobalBroadcast();
		}
		CONTEXT = this;
	};

	@Override
	public void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPause(this);
		ImageLoader.getInstance().clearMemoryCache();
		super.onPause();
	}

	private void addGlobalBroadcast() {
		mIntentFilter.addAction(ACTION_DEFAULT_BROAD);
		mIntentFilter.addAction(ConstantSet.ACTION_REGISTER_SUCCESS);
		addIntentFilter(mIntentFilter);
		registerReceiver(mGlobalReceiver, mIntentFilter);
	}

	/**
	 * 添加广播过滤器
	 * 
	 * @param intentFilter
	 *            参数
	 */
	protected void addIntentFilter(IntentFilter intentFilter) {

	}

	/**
	 * 
	 * @param action
	 *            广播类型
	 * @param data
	 *            广播发送的数据
	 */
	public void sendBroadCastV(String action, String data) {
		Intent intent = new Intent(ACTION_DEFAULT_BROAD);
		intent.putExtra("action", action);
		intent.putExtra("data", data);
		intent.putExtra(KEY_WORD_APP, CONTEXT.getPackageName());
		XhrApplicationBase.CONTEXT.sendBroadcast(intent);
	}

	/**
	 * 
	 * @param action
	 *            广播类型
	 * @param data
	 *            广播发送的数据
	 */
	public void sendBroadCastMap(String action, HashMap<String, Object> data) {
		Intent intent = new Intent(ACTION_DEFAULT_BROAD);
		intent.putExtra("action", action);
		intent.putExtra("data", data);
		intent.putExtra(KEY_WORD_APP, this.getPackageName());
		XhrApplicationBase.CONTEXT.sendBroadcast(intent);
	}

	/**
	 * 发送系统广播
	 * 
	 * @param intent
	 *            intent
	 * @see android.content.ContextWrapper#sendBroadcast(android.content.Intent)
	 */
	@Override
	public void sendBroadcast(Intent intent) {
		if (intent != null) {
			intent.putExtra(KEY_WORD_APP, CONTEXT.getPackageName());
			XhrApplicationBase.CONTEXT.sendBroadcast(intent);
		}
	}

	protected boolean showLoadingUpView(LoadingUpView loadingUpView) {
		if (loadingUpView != null) {
			loadingUpView.showPopup();
			return true;
		}
		return false;
	}

	protected boolean dismissLoadingUpView(LoadingUpView loadingUpView) {
		if (loadingUpView != null) {
			loadingUpView.dismiss();
			return true;
		}
		return false;
	}

	/**
	 * toast显示错误消息
	 * 
	 * @param result
	 */
	protected void showErrorMsg(ActionResult result) {
		showErrorToast(result, getResources().getString(R.string.network_is_not_available), false);
	}

	/**
	 * toast显示错误消息
	 * 
	 * @param result
	 * @param noMsgReturnToast
	 *            若result.obj中没有信息，是否显示默认信息
	 */
	protected void showErrorMsg(ActionResult result, boolean noMsgReturnToast) {
		showErrorToast(result, getResources().getString(R.string.network_is_not_available), noMsgReturnToast);
	}

	/**
	 * toast显示错误消息
	 * 
	 * @param result
	 * @param netErrorMsg
	 *            网络异常时显示消息
	 */
	protected void showErrorMsg(ActionResult result, String netErrorMsg) {
		showErrorToast(result, netErrorMsg, false);
	}

	/**
	 * 显示错误消息
	 * 
	 * @param result
	 * @param netErrorMsg
	 *            网络异常时显示消息
	 * @param 是否需要无返回值的提示
	 */
	private void showErrorToast(ActionResult result, String netErrorMsg, boolean noMsgReturnToast) {
		if (result == null) {
			return;
		}
		if (ActionResult.RESULT_CODE_NET_ERROR.equals(result.ResultCode)) {
			toast(netErrorMsg);
		} else if (result.ResultObject != null) {
			// 增加RESULT_CODE_ERROR值时也弹出网络异常
			if (ActionResult.RESULT_CODE_NET_ERROR.equals(result.ResultObject.toString())) {
				toast(netErrorMsg);
			} else {
				toast(result.ResultObject.toString());
			}
		} else if (noMsgReturnToast) {
			toast(getResources().getString(R.string.no_return_data));
		}
	}

	/**
	 * 默认的toast方法，该方法封装下面的两点特性：<br>
	 * 1、只有当前activity所属应用处于顶层时，才会弹出toast；<br>
	 * 2、默认弹出时间为 Toast.LENGTH_SHORT;
	 * 
	 * @param msg
	 *            弹出的信息内容
	 */
	public void toast(final String msg) {
		if (this != null && PackageUtil.isTopApplication(this)) {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!StringUtil.isNullOrEmpty(msg) && !DEFAULT_ERROR_MSG.equals(msg)
							&& !DEFAULT_NULL_MSG.equals(msg)) {
						if (mToast == null) {
							mToast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
						}
						mToast.setText(msg);
						TextView tv = (TextView) mToast.getView().findViewById(android.R.id.message);
						// 用来防止某些系统自定义了消息框
						if (tv != null) {
							tv.setGravity(Gravity.CENTER);
						}
						mToast.show();
					}
				}
			});
		}
	}

	/**
	 * 限制执行频率的方法。如按钮需要在指定的3000ms时间后才能再次执行，使用方式如：<br>
	 * 
	 * @param id
	 *            方法的标识，可以使用按钮控件的id或者其他唯一标识方法的字符串
	 * @param actionListener
	 *            方法的回调函数
	 */
	public void doActionAgain(final String id, ActionListener actionListener) {
		if (StringUtil.isNullOrEmpty(id) || actionListener == null) {
			throw new NullPointerException();
		}

		synchronized (mSync) {
			if (ACTION_LIST.contains(id)) {
				return;
			} else {
				ACTION_LIST.add(id);

				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						removeAction(id);
					}
				}, DEFAUTL_COOLING_TIME);
			}
		}

		actionListener.doAction();
	}

	/**
	 * 限制执行频率的方法。如按钮需要在指定的时间后才能再次执行，使用方式如：<br>
	 * 
	 * @param id
	 *            方法的标识，可以使用按钮控件的id或者其他唯一标识方法的字符串
	 * @param delay
	 *            延迟时间，以毫秒为单位
	 * @param actionListener
	 *            方法的回调函数
	 */
	protected void doActionAgain(final String id, int delay, ActionListener actionListener) {
		synchronized (mSync) {
			if (ACTION_LIST.contains(id)) {
				return;
			} else {
				ACTION_LIST.add(id);

				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						removeAction(id);
					}
				}, delay);
			}
		}

		actionListener.doAction();
	}

	private void removeAction(String id) {
		synchronized (mSync) {
			ACTION_LIST.remove(id);
		}
	}

	protected void processBroadReceiver(String action, Object data) {

	}

	public boolean ismIsUseBroadcase() {
		return mIsUseBroadcase;
	}

	public void setmIsUseBroadcase(boolean mIsUseBroadcase) {
		this.mIsUseBroadcase = mIsUseBroadcase;
	}

	public boolean isRefreshData() {
		return mIsRefreshData;
	}

	/**
	 * 设置在界面再次显示时候是否需要刷新(通过广播设置 ，这样避免进入多层之后，点击刷新多个页面问题)；调用 refreshData刷新数据
	 * 
	 * @param mIsRefreshData
	 */
	public void setmIsRefreshData(boolean mIsRefreshData) {
		this.mIsRefreshData = mIsRefreshData;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ismIsUseBroadcase()) {
			unregisterReceiver(mGlobalReceiver);
		}

	}

	/**
	 * 是否进行统计分析
	 * 
	 * @return
	 */
	public boolean isOpenStatistics() {
		return true;
	}

	/**
	 * 统计的名字
	 * 
	 * @return
	 */
	public String statisticsName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public Builder createDialogBuilder(Context context, String title, String message, String positiveBtnName,
			String negativeBtnName) {
		return DialogManager
				.createMessageDialogBuilder(context, title, message, positiveBtnName, negativeBtnName, this);
	}

	@Override
	public void onPositiveBtnClick(int id, DialogInterface dialog, int which) {

	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {

	}
}
