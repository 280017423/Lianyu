package com.xhr88.lp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;
import com.xhr.framework.app.XhrActivityBase;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.util.QJActivityManager;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.listener.IDialogProtocol;
import com.xhr88.lp.util.DialogManager;
import com.xhr88.lp.widget.CustomDialog.Builder;

class TraineeBaseActivity extends XhrActivityBase implements IDialogProtocol {
	protected ImageLoader mImageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		QJActivityManager.getInstance().pushActivity(this);
	}

	@Override
	public void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		QJActivityManager.getInstance().popActivity(this);
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

	protected boolean showLoadingUpView(LoadingUpView loadingUpView) {
		return showLoadingUpView(loadingUpView, "");
	}

	protected boolean showLoadingUpView(LoadingUpView loadingUpView, String info) {
		if (loadingUpView != null && !loadingUpView.isShowing()) {
			if (null == info) {
				info = "";
			}
			loadingUpView.showPopup(info);
			return true;
		}
		return false;
	}

	protected boolean dismissLoadingUpView(LoadingUpView loadingUpView) {
		if (loadingUpView != null && loadingUpView.isShowing()) {
			loadingUpView.dismiss();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (getParent() != null) {
			return getParent().onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 处理ActivityResult方法
	 * 
	 * @param requestCode
	 *            请求码
	 * @param resultCode
	 *            结果码
	 * @param data
	 *            intent数据
	 * 
	 */
	public void handleActivityResult(int requestCode, int resultCode, Intent data) {
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
