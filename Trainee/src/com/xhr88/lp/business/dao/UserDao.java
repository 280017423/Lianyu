package com.xhr88.lp.business.dao;

import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.orm.DataAccessException;
import com.xhr.framework.orm.DataManager;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.model.datamodel.ConversationUserInfoModel;
import com.xhr88.lp.model.datamodel.FileuploadModel;
import com.xhr88.lp.model.datamodel.UserInfoModel;
import com.xhr88.lp.model.datamodel.WeiboInfoModel;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.DBUtil;
import com.xhr88.lp.util.SharedPreferenceUtil;

/**
 * 
 * @author
 * @version
 */
public class UserDao {
	private static final String TAG = "UserDao";

	/**
	 * 清楚本地缓存的filetoken信息
	 */
	public static void clearLocalFileuploadInfo() {
		SharedPreferenceUtil.clearObject(XhrApplicationBase.CONTEXT, FileuploadModel.SHAREPREFERENCES_NAME);
	}

	/**
	 * 保存filetoken信息
	 * 
	 * @param
	 * 
	 * 
	 * @return 成功与否
	 */

	public static boolean saveLocalFileuploadInfo(FileuploadModel mFileuploadModel) {
		try {
			SharedPreferenceUtil.saveObject(XhrApplicationBase.CONTEXT, FileuploadModel.SHAREPREFERENCES_NAME,
					mFileuploadModel);
		} catch (Exception e) {
			EvtLog.e(TAG, e);
			return false;
		}
		return true;
	}

	public boolean hasFileuploadInfo() {
		FileuploadModel mFileuploadModel = UserDao.getLocalFileuploadInfo();
		return mFileuploadModel != null && !StringUtil.isNullOrEmpty(mFileuploadModel.getFiletoken());
	}

	public static FileuploadModel getLocalFileuploadInfo() {

		FileuploadModel mFileuploadModel = (FileuploadModel) SharedPreferenceUtil.getObject(XhrApplicationBase.CONTEXT,
				FileuploadModel.SHAREPREFERENCES_NAME, FileuploadModel.class);
		if (mFileuploadModel == null) {
			mFileuploadModel = new FileuploadModel();
		}
		return mFileuploadModel;
	}

	/**
	 * 清除本地缓存的用户信息
	 */
	public static void clearLocalUserInfo() {
		SharedPreferenceUtil.clearObject(XhrApplicationBase.CONTEXT, UserViewModel.SHAREPREFERENCES_NAME);
	}

	/**
	 * 保存登录状态
	 * 
	 * @param user
	 *            登录信息
	 * 
	 * @return 成功与否
	 */
	public static boolean saveLocalUserInfo(UserViewModel user) {
		try {
			SharedPreferenceUtil.saveObject(XhrApplicationBase.CONTEXT, UserViewModel.SHAREPREFERENCES_NAME, user);
			if (user.UserInfo != null) {
				SharedPreferenceUtil.saveObject(XhrApplicationBase.CONTEXT, UserViewModel.SHAREPREFERENCES_NAME,
						user.UserInfo);
			}
			if (user.mWeiboInfoModel != null) {
				SharedPreferenceUtil.saveObject(XhrApplicationBase.CONTEXT, UserViewModel.SHAREPREFERENCES_NAME,
						user.mWeiboInfoModel);
			}
		} catch (Exception e) {
			EvtLog.e(TAG, e);
			return false;
		}
		return true;
	}

	/**
	 * 检查本地是否存在用户登录信息
	 * 
	 * @return 返回本地是否有用户信息，该信息在用户登出前都不会清除，可以作为用户是否登录过的判断
	 */
	public static boolean hasUserInfo() {
		EvtLog.d(TAG, "检查本地是否存在用户登录信息");
		UserViewModel user = UserDao.getLocalUserInfo();
		return user != null && !StringUtil.isNullOrEmpty(user.UserInfo.getToken());
	}

	/**
	 * 获取用户性别
	 * 
	 * @return 用户性别 1：为男, 2：女
	 */
	public static int getSex() {
		int sex = 1;
		if (hasUserInfo()) {
			UserViewModel user = UserDao.getLocalUserInfo();
			sex = user.UserInfo.getSex();
		}
		return sex;
	}

	/**
	 * 获取用户登录信息
	 * 
	 * @return 返回登录 信息
	 */
	public static UserViewModel getLocalUserInfo() {
		// String typeSina = "3";
		// String typeQQ = "2";
		// String typeWeiXin = "1";
		UserViewModel user = (UserViewModel) SharedPreferenceUtil.getObject(XhrApplicationBase.CONTEXT,
				UserViewModel.SHAREPREFERENCES_NAME, UserViewModel.class);
		if (user == null) {
			user = new UserViewModel();
		}
		user.UserInfo = (UserInfoModel) SharedPreferenceUtil.getObject(XhrApplicationBase.CONTEXT,
				UserViewModel.SHAREPREFERENCES_NAME, UserInfoModel.class);
		if (user.UserInfo == null) {
			user.UserInfo = new UserInfoModel();
		}

		user.mWeiboInfoModel = (WeiboInfoModel) SharedPreferenceUtil.getObject(XhrApplicationBase.CONTEXT,
				UserViewModel.SHAREPREFERENCES_NAME, WeiboInfoModel.class);
		if (user.mWeiboInfoModel == null) {
			user.mWeiboInfoModel = new WeiboInfoModel();
		}
		return user;
	}

	/**
	 * 
	 * @param key
	 *            键名
	 * @param values
	 *            值
	 * @return boolean 结果
	 */
	public boolean saveData(String key, Object values) {
		EvtLog.d(TAG, "保存当前登录状态 ：key:" + key + "- value:" + values);
		try {
			SharedPreferenceUtil
					.saveValue(XhrApplicationBase.CONTEXT, UserViewModel.SHAREPREFERENCES_NAME, key, values);
		} catch (Exception e) {
			EvtLog.w(TAG, e);
			return false;
		}
		return true;
	}

	public static ConversationUserInfoModel getConversationUserInfoModel(String uid) {
		if (StringUtil.isNullOrEmpty(uid)) {
			return null;
		}
		ConversationUserInfoModel model = null;
		DataManager dataManager = DBUtil.getDataManager();
		dataManager.open();
		try {
			model = dataManager.get(ConversationUserInfoModel.class, "UID = ?", new String[] { uid });
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		dataManager.close();
		return model;
	}

}
