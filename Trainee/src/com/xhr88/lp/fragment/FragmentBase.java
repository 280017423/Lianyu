package com.xhr88.lp.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionResult;

public abstract class FragmentBase extends Fragment {

	protected ImageLoader mImageLoader = ImageLoader.getInstance();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			} else if ("900".equals(result.ResultCode)) {
				// 暂时修改发送给自己的消息提示异常
				toast(result.ResultStateCode);
			} else {
				toast(result.ResultObject.toString());
			}
		} else if (noMsgReturnToast) {
			toast(getResources().getString(R.string.no_return_data));
		}

	}

	/**
	 * 弹出toast
	 * 
	 * @param e
	 *            出错的exception
	 */
	public void toast(final Throwable e) {
		if (!isAdded()) {
			return;
		}
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getActivity(),
						getActivity().getResources().getString(R.string.msg_operate_fail_try_again), Toast.LENGTH_SHORT)
						.show();
			}
		});
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
		if (!isAdded()) {
			return;
		}
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!StringUtil.isNullOrEmpty(msg)) {
					Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
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

	/**
	 * 
	 * @Method: onPositiveBtnClick
	 * @Description: 确定按钮回调
	 * @param id
	 *            当前对话框对象的ID
	 * @param dialog
	 *            DialogInterface 对象
	 * @param which
	 *            dialog ID
	 */
	public void onPositiveBtnClick(int id, DialogInterface dialog, int which) {
	}

	/**
	 * 
	 * @Method: onPositiveBtnClick
	 * @Description: 取消按钮回调
	 * @param id
	 *            当前对话框对象的ID
	 * @param dialog
	 *            DialogInterface 对象
	 * @param which
	 *            dialog ID
	 */
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
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

}
