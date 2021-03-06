package com.xhr.framework.authentication;

import android.app.Activity;
import android.content.Intent;

import com.xhr.framework.authentication.BaseLoginProcessor.LOGIN_TYPE;
import com.xhr.framework.model.ActionModel;

/***
 * 
 * 此类用户封装请求逻辑基类,请求总入口
 * 
 * @author xiaoxing
 * @version 2013-3-11 下午1:28:52 xiaoxing
 * @param <T>网络请求结果
 * @param <T2>网络请求接口回调
 */
@SuppressWarnings("rawtypes")
public abstract class BaseActionProcessor<T extends BaseActionResult, T2 extends IBaseActionListener> {

	/**
	 * 构造函数
	 */
	public BaseActionProcessor() {
	}

	/**
	 * jumpActivity 跳转界面
	 * 
	 * @param action
	 *            动作类
	 */
	protected abstract void jumpActivity(ActionModel action);

	/**
	 * 
	 * 执行动作
	 * 
	 * @param activity
	 *            当前上下文
	 * 
	 * @param isOnUiThreadCallBack
	 *            是否主线程执行回调
	 * @param returnToMain
	 *            取消登录后，是否返回首页
	 * @param listener
	 *            回调函数
	 */
	public abstract void startAction(Activity activity, boolean isOnUiThreadCallBack, boolean returnToMain,
			final T2 listener);

	/**
	 * 跳转到登录界面
	 * 
	 * @param activity
	 *            Activity
	 * @param intent
	 *            Intent
	 * @param loginType
	 *            登录类型
	 */
	public abstract void jumpToLoginActivity(Activity activity, Intent intent, LOGIN_TYPE loginType);

	/**
	 * 获取进入登录界面Intent （ActivityGroup等跳转中使用,需要传递其它参数时使用）
	 * 
	 * @param activity
	 *            Activity
	 * @param loginType
	 *            登录类型
	 * @return Intent
	 */
	public abstract Intent getLoginIntent(Activity activity, LOGIN_TYPE loginType);

	/**
	 * 登录验证页面跳转
	 * 
	 * @param activity
	 *            当前下下文
	 * @param intent
	 *            跳转目的
	 * @param returnToMain
	 *            取消登录后，是否返回首页
	 */
	public void startActivity(Activity activity, final Intent intent, boolean returnToMain) {
		// 登录类型(跳转界面)
		LOGIN_TYPE loginType = LOGIN_TYPE.From_Jump_Activity_Type;
		if (returnToMain) {
			loginType = LOGIN_TYPE.From_Jump_Activity_Type_And_Cancel_ReturnMain_Type;
		}
		ActionModel action = new ActionModel<IBaseActionListener>(activity, null, intent, false);
		action.setLoginType(loginType);
		jumpActivity(action);

	}

	/**
	 * 登录验证页面跳转
	 * 
	 * @param activity
	 *            当前下下文
	 * @param intent
	 *            跳转目的
	 * @param loginType
	 *            登录类型
	 */
	public void startActivity(Activity activity, final Intent intent, LOGIN_TYPE loginType) {
		ActionModel action = new ActionModel<IBaseActionListener>(activity, null, intent, false);
		action.setLoginType(loginType);
		jumpActivity(action);
	}

	/**
	 * 登录验证页面跳转
	 * 
	 * @param activity
	 *            当前下下文
	 * @param intent
	 *            跳转目的
	 */

	public void startActivity(Activity activity, final Intent intent) {
		startActivity(activity, intent, false);
	}

	/**
	 * 执行动作
	 * 
	 * @param activity
	 *            上下文
	 * @param listener
	 *            回调对象
	 */
	public void startAction(Activity activity, final T2 listener) {
		startAction(activity, false, false, listener);
	}

	/**
	 * 执行动作
	 * 
	 * @param activity
	 *            上下文
	 * @param returnToMain
	 *            是否返回首页
	 * @param listener
	 *            回调对象
	 */
	public void startAction(Activity activity, boolean returnToMain, T2 listener) {
		startAction(activity, false, returnToMain, listener);
	}

	/**
	 * 执行动作,UI线程回调函数
	 * 
	 * @param activity
	 *            上下文
	 * @param listener
	 *            回调对象
	 */
	public void startActionOnUiCallBack(Activity activity, final T2 listener) {
		startAction(activity, true, false, listener);
	}

}
