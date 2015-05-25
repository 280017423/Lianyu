package com.xhr.framework.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.xhr.framework.R;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.PackageUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * 所有应用Activity的基类
 * 
 * @author wang.xy
 * @version <br>
 *          2012-09-05 王先佑 添加doActionAgain方法<br>
 *          2013-04-18，tan.xx，获取屏幕物理尺寸方法<br>
 *          2013-08-27, huang.b 修改toast方法，内部文字为居中显示；<Br>
 * 
 */
public abstract class XhrActivityBaseWithSlidingMenu extends SlidingFragmentActivity {
	public static final String ACTION_DEFAULT_BROAD = "TestCustomBroadCast";
	public static final String ACTION_EXIT_APP = "EXIT_APP";
	public static final String ACTION_RESET_APP = "ACTION_RESET_APP";
	public static final String KEY_WORD_APP = "KEY_WORD_APP";
	private static final int DEFAUTL_COOLING_TIME = 3000;
	private static final String TAG = "PdwActivityBase";
	private static final String DEFAULT_ERROR_MSG = "error";
	private static final Object mSync = new Object();
	private static final List<String> ACTION_LIST = new ArrayList<String>();
	public static Context mContext;

	protected final IntentFilter mIntentFilter = new IntentFilter();
	protected BroadcastReceiver mGlobalReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Object data = intent.getSerializableExtra("data");
			String appIndentify = intent.getStringExtra(KEY_WORD_APP);
			EvtLog.d(TAG, "onReceive action: " + intent.getAction() + " appIndentify: " + appIndentify
					+ " packageName: " + mContext.getPackageName() + " intentPackage:" + intent.getPackage());
			// 过滤当前接收到的广播是否和上下文一致
			if (!StringUtil.isNullOrEmpty(appIndentify) && context.getPackageName().equals(appIndentify)) {
				if (action.equals(ACTION_DEFAULT_BROAD)) {
					action = intent.getStringExtra("action");
				}
				// 若收到退出程序的广播，则直接退出程序
				if (ACTION_EXIT_APP.equals(action) && !isFinishing()) {
					onFinish();
					finish();
					return;
				}
			}
			// 广播处理方法
			processBroadReceiver(action, data);
		}
	};

	/**
	 * 获取屏幕物理尺寸
	 * 
	 * @return double
	 * @throws
	 */
	private double getScreenSize() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 屏幕尺寸=屏幕对角线的像素值/（密度*160）= （长的平方+宽的平方）开2次根 / (密度*160）
		double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
		double scrrenSize = diagonalPixels / (160 * dm.density);
		return scrrenSize;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		EvtLog.d(TAG, "onCreate start... ");
		super.onCreate(savedInstanceState);
		mContext = this;
		// mAnalyticsGoogle = PDWGoogleAnalyticsImpl.getInstance(this);
		mIntentFilter.addAction(ACTION_DEFAULT_BROAD);
		mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		addIntentFilter(mIntentFilter);
		registerReceiver(mGlobalReceiver, mIntentFilter);
		EvtLog.d(TAG, "onCreate end... ");

		if (XhrApplicationBase.SCRRENSIZE <= 0d) {
			XhrApplicationBase.SCRRENSIZE = getScreenSize();
			EvtLog.d(TAG, "获取屏幕物理尺寸---- " + XhrApplicationBase.SCRRENSIZE);
		}
	}

	protected void processBroadReceiver(String action, Object data) {

	}

	/**
	 * 关闭时做的处理
	 */
	protected void onFinish() {

	}

	/**
	 * 手动添加广播频道
	 * 
	 * @param str
	 */
	protected void addAction(String... str) {
		String action;
		for (int i = 0; i < str.length; i++) {
			action = str[i];
			mIntentFilter.addAction(action);
		}
	}

	/**
	 * 添加广播过滤器
	 * 
	 * @param intentFilter
	 *            参数
	 */
	protected void addIntentFilter(IntentFilter intentFilter) {

	}

	// @Override
	// protected void onPause() {
	// super.onPause();
	// mAnalyticsGoogle.onPause(mContext);
	// mAnalyticsPDW.analyticsOnPause(mContext);
	// }
	//
	// @Override
	// protected void onResume() {
	// super.onResume();
	// mAnalyticsGoogle.onResume(mContext);
	// mAnalyticsPDW.analyticsOnResume(mContext);
	// }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// EvtLog.e(TAG, "onSaveInstanceState");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mGlobalReceiver);
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
		intent.putExtra(KEY_WORD_APP, mContext.getPackageName());
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
	 */
	@Override
	public void sendBroadcast(Intent intent) {
		if (intent != null) {
			intent.putExtra(KEY_WORD_APP, mContext.getPackageName());
			XhrApplicationBase.CONTEXT.sendBroadcast(intent);
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
		if (PackageUtil.isTopApplication(this)) {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!StringUtil.isNullOrEmpty(msg) && !DEFAULT_ERROR_MSG.equals(msg)) {
						Toast toast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
						TextView tv = (TextView) toast.getView().findViewById(android.R.id.message);
						// 用来防止某些系统自定义了消息框
						if (tv != null) {
							tv.setGravity(Gravity.CENTER);
						}
						toast.show();
					}
				}
			});
		}
	}

	/**
	 * 弹出toast
	 * 
	 * @param e
	 *            出错的exception
	 */
	public void toast(final Throwable e) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(),
						getBaseContext().getResources().getString(R.string.msg_operate_fail_try_again),
						Toast.LENGTH_SHORT).show();
			}
		});
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
	public void doActionAgain(final String id, int delay, ActionListener actionListener) {
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

	/**
	 * @author Administrator
	 */
	public interface ActionListener {
		/**
		 * 
		 */
		void doAction();
	}
}
