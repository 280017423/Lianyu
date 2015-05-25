package com.xhr88.lp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.util.PackageUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.MainActivity;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.common.ConstantSet;

public abstract class TraineeFragmentBase extends Fragment {

	private MainActivity mActivity;
	private Toast mToast;
	private LoadingUpView mLoadingUpView;
	// 用于判断当前fragment是否可见
	protected boolean isVisible;
	protected boolean isStart = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		initViews();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void showLoadingView() {
		if (mLoadingUpView != null && !mLoadingUpView.isShowing()) {
			mLoadingUpView.showPopup();
		}
	}

	public void dismissLoadingView() {
		if (mLoadingUpView != null && mLoadingUpView.isShowing()) {
			mLoadingUpView.dismiss();
		}
	}

	/**
	 * 在这里实现Fragment数据的缓加载.
	 * 
	 * @param isVisibleToUser
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
			startPage();
		} else {
			endPage();
			isVisible = false;
			onInvisible();
		}
		ImageLoader.getInstance().clearMemoryCache();
	}

	private void startPage() {
		if (!isOpenStatistics()) {
			return;
		}
		// 是否在ViewPager中，需要不同的设置
		if (isInViewPager()) {
			if (!isStart && isVisible) {
				isStart = true;
			}
		} else {
			isStart = true;
		}
	}

	private void endPage() {
		if (!isOpenStatistics()) {
			return;
		}
		if (isInViewPager()) {
			if (isStart && isVisible) {
				isStart = false;
			}
		} else {
			isStart = false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		startPage();
	}

	@Override
	public void onPause() {
		super.onPause();
		endPage();
		ImageLoader.getInstance().clearMemoryCache();
	}

	/**
	 * 当前fragment显示
	 */
	protected void onVisible() {
		lazyLoad();
	}

	/**
	 * 延迟加载，只有当显示时候才会
	 */
	protected abstract void lazyLoad();

	protected abstract void loadData();

	/**
	 * 当前fragment隐藏
	 */
	protected void onInvisible() {
	}

	private void initViews() {
		if (getActivity() != null) {
			mLoadingUpView = new LoadingUpView(getActivity(), true);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MainActivity) {
			mActivity = (MainActivity) activity;
		}
	}

	public MainActivity getMainActivity() {
		return mActivity;
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
		if (getActivity() == null) {
			return;
		}
		if (this != null && PackageUtil.isTopApplication(getActivity())) {
			getMainActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!StringUtil.isNullOrEmpty(msg) && !ConstantSet.DEFAULT_ERROR_MSG.equals(msg)
							&& !ConstantSet.DEFAULT_NULL_MSG.equals(msg)) {
						if (mToast == null) {
							mToast = Toast.makeText(getMainActivity(), msg, Toast.LENGTH_SHORT);
							TextView tv = (TextView) mToast.getView().findViewById(android.R.id.message);
							// 用来防止某些系统自定义了消息框
							if (tv != null) {
								tv.setGravity(Gravity.CENTER);
							}
						} else {
							mToast.setText(msg);
						}
						mToast.show();
					}
				}
			});
		}
	}

	/**
	 * toast显示错误消息
	 * 
	 * @param result
	 */
	protected void showErrorMsg(ActionResult result) {
		if (getActivity() != null) {
			showErrorToast(result, getActivity().getResources().getString(R.string.network_is_not_available), false);
		}
	}

	/**
	 * toast显示错误消息
	 * 
	 * @param result
	 * @param noMsgReturnToast
	 *            若result.obj中没有信息，是否显示默认信息
	 */
	protected void showErrorMsg(ActionResult result, boolean noMsgReturnToast) {
		if (getActivity() != null) {
			showErrorToast(result, getActivity().getResources().getString(R.string.network_is_not_available),
					noMsgReturnToast);
		}
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
			if (getActivity() != null) {
				toast(getActivity().getResources().getString(R.string.no_return_data));
			}
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

	public boolean isInViewPager() {
		return false;
	}

	/**
	 * 统计的名字
	 * 
	 * @return
	 */
	public String statisticsName() {
		return this.getClass().getSimpleName();
	}

}
