package com.xhr88.lp.authentication;

import android.app.Activity;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.xhr.framework.authentication.BaseActionTask;
import com.xhr.framework.model.ActionModel;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.listener.IActionListener;

/**
 * Description the class TODO
 * 
 * @author tan.xx
 * @version 2013-12-12 下午3:20:33 tan.xx TODO
 */
public class ActionTask extends BaseActionTask<ActionResult> {

	/**
	 * 构造方法
	 * 
	 */
	protected ActionTask() {
		super();

	}

	/**
	 * 请求数据完成回调
	 * 
	 * @param result
	 * @param action
	 * @see com.qianjiang.framework.authentication.BaseActionTask#doResultCallBack(com.qianjiang.framework.authentication.BaseActionResult,
	 *      com.qianjiang.framework.authentication.BaseLoginProcessor.ActionModel)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void doResultCallBack(final ActionResult result, final ActionModel action) {
		IActionListener listener = null;
		if (action == null) {
			return;
		}
		listener = (IActionListener) action.getListener();
		if (listener == null) {
			return;
		}
		if (result == null) {
			listener.onError(new ActionResult());
		} else if (ActionResult.RESULT_CODE_NOLOGIN.equals(result.ResultCode)) {
			EvtLog.d(TAG, "token过期");
			if (action.isAfterAutoLogin()) {
				// 自动登录后接口还是返回未登录(接口正常应该不会出现此现象)
				// 进入到登录界面
				LoginProcessor.getInstance().processorToLogin(action);
			} else {
				// 进入自动登录
				LoginProcessor.getInstance().executeAutoLoginTask(action);
			}
		} else if (ActionResult.RESULT_CODE_ACCESS_ERROR.equals(result.ResultCode)) {
			// 账号未授权
			doNotVerify(action.getActivity(), result);
			listener.onError(result);
			LoginProcessor.getInstance().processorToLogin(action);

		} else {
			EvtLog.d(TAG, "AsyncRun onBack...");
			if (ActionResult.RESULT_CODE_SUCCESS.equals(result.ResultCode)) {
				// 返回成功
				listener.onSuccess(result);
			} else if (!ActionResult.RESULT_CODE_IS_RELEASE.equals(result.ResultCode)) {
				// 返回错误时
				listener.onError(result);
			}

		}

	}

	/**
	 * 未授权处理
	 * 
	 * @param activity
	 * @param result
	 */
	private void doNotVerify(final Activity activity, final ActionResult result) {

		if (result != null) {
			try {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 显示错误消息
						String info = (String) result.ResultObject;
						Toast toast = Toast.makeText(activity, info, Toast.LENGTH_SHORT);
						TextView tv = (TextView) toast.getView().findViewById(android.R.id.message);
						tv.setGravity(Gravity.CENTER);
						toast.show();
					}
				});
			} catch (ClassCastException e) {
				EvtLog.d(TAG, e.toString());
			}
		}
		// Intent intent = new Intent(activity, MainActivityGroup.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// new ActionProcessor().jumpToLoginActivity(activity, intent,
		// LOGIN_TYPE.Exit_To_Cancel_Apk);
	}

	/**
	 * 自动登录回调
	 * 
	 * @param result
	 * @param action
	 * @see com.qianjiang.framework.authentication.BaseActionTask#doAutoLoginCallBack(com.qianjiang.framework.authentication.BaseActionResult,
	 *      com.qianjiang.framework.model.ActionModel)
	 */
	@Override
	protected void doAutoLoginCallBack(final ActionResult result, final ActionModel<?> action) {
		IActionListener listener = null;
		if (action == null) {
			return;
		}
		// 登录前回调
		listener = (IActionListener) action.getListener();
		if (listener == null) {
			return;
		}
		if (result == null) {
			// 进入到登录界面
			LoginProcessor.getInstance().processorToLogin(action);
		} else if (ActionResult.RESULT_CODE_SUCCESS.equals(result.ResultCode)) {
			action.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// 增加登录成功提示语
					String loginInfo = (String) result.ResultObject;
					if (!StringUtil.isNullOrEmpty(loginInfo)) {
						Toast.makeText(action.getActivity(), loginInfo, Toast.LENGTH_LONG).show();
					}
				}
			});
			// 登录成功，继续之前网络请求
			action.setIsAfterAutoLogin(true);
			LoginProcessor.getInstance().doAction(action, false);
		} else if (!ActionResult.RESULT_CODE_IS_RELEASE.equals(result.ResultCode)) {
			// 登录错误时，进入到登录界面
			LoginProcessor.getInstance().processorToLogin(action);

		}
	}

	/**
	 * 取消登录回调 doLoginCancelAction
	 * 
	 * @param action
	 * @see com.qianjiang.framework.authentication.BaseActionTask#doLoginCancelAction(com.qianjiang.framework.model.ActionModel)
	 */
	@Override
	protected void doLoginCancelAction(ActionModel<?> action) {
		if (action == null) {
			return;
		}
		IActionListener listener = (IActionListener) action.getListener();
		if (listener == null) {
			return;
		}
		ActionResult result = new ActionResult();
		result.ResultCode = ActionResult.RESULT_CODE_NOLOGIN;
		listener.onError(result);

	}

	/**
	 * 自动登录请求
	 * 
	 * @return
	 * @see com.qianjiang.framework.authentication.BaseActionTask#doAutoLoginReq()
	 */
	@Override
	protected ActionResult doAutoLoginReq() {
		// 不自动登录直接到登录界面
		return null;
		// return UserReq.autoLogin();
	}
}
