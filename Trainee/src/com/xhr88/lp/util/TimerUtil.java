package com.xhr88.lp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.xhr.framework.app.XhrActivityBase.ActionListener;
import com.xhr.framework.util.EvtLog;

/**
 * @author zou.sq
 * @version 2013-11-28 09:26 zou.sq 新建定时工具类,获取验证码时用
 * 
 */
public class TimerUtil {
	private static final String TAG = "TimerUtil";
	// 记录时间的容器
	private static final Map<String, Integer> TIME_MAP = new HashMap<String, Integer>();
	// 记录Timer的容器
	private static final Map<String, Timer> TIMEER_MAP = new HashMap<String, Timer>();
	// 记录TimerTask的容器
	private static final Map<String, TimerTask> TIMER_TASK_MAP = new HashMap<String, TimerTask>();

	private static void initTimer(final String key, int time, final ActionListener actionListener) {
		Timer timer = new Timer(key);
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				int time = TIME_MAP.get(key);
				TIME_MAP.put(key, --time);
				EvtLog.d(TAG, "执行计时任务" + key + ":" + time);
				actionListener.doAction();
			}
		};

		TIME_MAP.put(key, time + 1);
		TIMEER_MAP.put(key, timer);
		TIMER_TASK_MAP.put(key, timerTask);
	}

	/**
	 * 
	 * @Title: startTimer
	 * @Description: 开始执行及时任务
	 * @param key
	 *            计时的键
	 * @param time
	 *            计时的总时间
	 * @param timer_period
	 *            执行任务时间间隔
	 * @param actionListener
	 *            计时的回调接口
	 */
	public static void startTimer(String key, int time, long timer_period, ActionListener actionListener) {
		stopTimer(key);
		initTimer(key, time, actionListener);
		TIMEER_MAP.get(key).schedule(TIMER_TASK_MAP.get(key), 0, timer_period);
	}

	/**
	 * 
	 * @Title: stopTimer
	 * @Description: 停止计时任务
	 * @param key
	 *            计时的键
	 */
	public static void stopTimer(String key) {
		if (TIMER_TASK_MAP.get(key) != null) {
			TIMER_TASK_MAP.get(key).cancel();
		}
		resetTimer(key);
	}

	/**
	 * 
	 * @Title: getTimerTime
	 * @Description: 获取剩余时间
	 * @param key
	 *            计时的键
	 * @return int 返回剩余时间
	 */
	public static int getTimerTime(String key) {
		if (TIME_MAP.get(key) == null) {
			return 0;
		}
		return TIME_MAP.get(key);
	}

	/**
	 * 
	 * @Title: setTimerTime
	 * @Description: 设置计时时间
	 * @param key
	 *            计时的键
	 * @param time
	 *            计时的总时间
	 */
	public static void setTimerTime(String key, int time) {
		TIME_MAP.put(key, time);
	}

	private static void resetTimer(String key) {
		TIME_MAP.remove(key);
		TIMEER_MAP.remove(key);
		TIMER_TASK_MAP.remove(key);
	}

}
