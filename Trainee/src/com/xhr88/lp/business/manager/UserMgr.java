package com.xhr88.lp.business.manager;

import io.rong.imkit.RongIM;

import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.HttpClientUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.authentication.LoginProcessor;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.DBUtil;
import com.xhr88.lp.util.SharedPreferenceUtil;

/**
 * 用户操作管理类
 * 
 * @author zou.sq
 * @version
 */
public class UserMgr {
	private static final String TAG = "UserMgr";

	/**
	 * 用户登出
	 */
	public static void logout() {
		// 清除本地的用户信息
		clearUserInfo();
		clearConfigInfo();
		// 清除所有的用户数据
		DBUtil.clearAllTables();
		// 重置本地状态
		LoginProcessor.getInstance().setLoginStatus(false);
		// 还原Cookies
		HttpClientUtil.setCookieStore(null);
		// 断开融云
		if (RongIM.getInstance() != null) {
			RongIM.getInstance().disconnect(false);
		}
	}

	/**
	 * 本地是否有用户信息(如果需使用用户信息，建议直接使用UserDao中的getLocalUserInfo())
	 * 
	 * @return 返回本地是否有用户信息，该信息在用户登出前都不会清除，可以作为用户是否登录过的判断
	 */
	public static boolean hasUserInfo() {
		UserViewModel userViewModel = UserDao.getLocalUserInfo();
		EvtLog.d(TAG, "userViewModel.UserInfo.token = " + userViewModel.UserInfo.getToken());
		return userViewModel != null && userViewModel.UserInfo != null
				&& !StringUtil.isNullOrEmpty(userViewModel.UserInfo.getUid());
	}

	/**
	 * 清空用户信息
	 */
	public static void clearUserInfo() {
		UserDao.clearLocalUserInfo();
	}

	/**
	 * 清除本地配置信息
	 * 
	 * @Date 2014-9-28 下午12:01:06
	 * 
	 */
	public static void clearConfigInfo() {
		SharedPreferenceUtil.clearObject(XhrApplicationBase.CONTEXT, ServerAPIConstant.KEY_CONFIG_FILENAME);
	}

	/**
	 * 获取指定键的值
	 * 
	 * @param key
	 *            键名
	 * @return 结果
	 */
	public static String getStringData(String key) {
		return SharedPreferenceUtil.getStringValueByKey(XhrApplicationBase.getInstance(),
				UserViewModel.SHAREPREFERENCES_NAME, key);
	}

	public static Integer getIntegerData(String key) {
		return SharedPreferenceUtil.getIntegerValueByKey(XhrApplicationBase.getInstance(),
				UserViewModel.SHAREPREFERENCES_NAME, key);
	}

	/**
	 * 得到 本地保存，用于接口使用
	 * 
	 * @return
	 */
	public static String getUid() {
		if (hasUserInfo()) {
			UserViewModel userViewModel = UserDao.getLocalUserInfo();
			return userViewModel.UserInfo.getUid();
		}
		return "";
	}

	/**
	 * 得到 本地保存MemberId，用于接口使用
	 * 
	 * @return
	 */
	public static String getToken() {
		return getStringData(ServerAPIConstant.KEY_TOKEN);
	}

	/**
	 * 验证是否是本站登录
	 * 
	 * @return
	 */
	public static boolean isXhrLogin() {
		if (!hasUserInfo()) {
			return false;
		}
		UserViewModel userViewModel = UserDao.getLocalUserInfo();
		return 0 == userViewModel.LoginType;
	}

	public static boolean isMineUid(String uid) {
		if (StringUtil.isNullOrEmpty(uid) || !hasUserInfo()) {
			return false;
		}
		UserViewModel userViewModel = UserDao.getLocalUserInfo();
		if (!userViewModel.UserInfo.getUid().equals(uid)) {
			return false;
		}
		return true;
	}

}
