package com.xhr88.lp.util;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.MsgReq;
import com.xhr88.lp.listener.RongCloudEvent;

/**
 * 融云工具类
 * 
 * @author zou.sq
 * @since 2015-03-28
 */
public class RongIMUtil {
	private static final String TAG = "RongIM";
	private RongIM.LocationProvider.LocationCallback mLastLocationCallback;

	public static void connect() throws Exception {
		if (UserMgr.hasUserInfo()) {
			String token = UserDao.getLocalUserInfo().UserInfo.getImtoken();
			if (!StringUtil.isNullOrEmpty(token)) {
				EvtLog.d(TAG, "connect token:" + token);
				RongIM.connect(token, new RongIMClient.ConnectCallback() {

					@Override
					public void onSuccess(String s) {
						EvtLog.d(TAG, "connect success");
						RongCloudEvent.getInstance().setOtherListener();
					}

					@Override
					public void onError(ErrorCode errorCode) {
						EvtLog.d(TAG, "connect failed:" + errorCode.toString());
						if (errorCode.getValue() == 2004) {
							getToken();
						}
					}

				});
			} else {
				getToken();
			}
		} else {
			getToken();
		}
	}

	private static void getToken() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ActionResult result = MsgReq.getToken();
				if (ActionResult.RESULT_CODE_SUCCESS.equals(result.ResultCode)) {
					try {
						connect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					getToken();
				}
			}
		}).start();
	}

	public static void disConnect() {
		RongIM.getInstance().disconnect(false);
	}

	public RongIM.LocationProvider.LocationCallback getLastLocationCallback() {
		return mLastLocationCallback;
	}

	public void setLastLocationCallback(RongIM.LocationProvider.LocationCallback lastLocationCallback) {
		this.mLastLocationCallback = lastLocationCallback;
	}

}
