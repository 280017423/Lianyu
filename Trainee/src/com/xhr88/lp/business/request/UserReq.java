package com.xhr88.lp.business.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.pdw.gson.reflect.TypeToken;
import com.xhr.framework.app.JsonResult;
import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.authentication.BaseActionResult;
import com.xhr.framework.encrypt.Md5Tool;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.HttpClientUtil;
import com.xhr.framework.util.PackageUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.app.TraineeApplication;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.model.datamodel.FileuploadModel;
import com.xhr88.lp.model.datamodel.OtherUserModel;
import com.xhr88.lp.model.datamodel.PictureModel;
import com.xhr88.lp.model.datamodel.UserCategoryModel;
import com.xhr88.lp.model.datamodel.UserInfoModel;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.SharedPreferenceUtil;

/**
 * 用户请求类
 * 
 * @author zou.sq
 */
public class UserReq {

	public static final String TAG = "UserReq";

	/**
	 * 获取用户信息
	 * 
	 * @return UserDataModel
	 * @throws
	 */
	public static UserViewModel getLocalUserInfo() {
		return UserDao.getLocalUserInfo();
	}

	public FileuploadModel getLocalFileuploadInfo() {
		return UserDao.getLocalFileuploadInfo();
	}

	public static ActionResult changePwd(String oldpassword, String newpassword) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_EDIT_PASSWORD);
		try {
			String userName = SharedPreferenceUtil.getStringValueByKey(TraineeApplication.CONTEXT,
					ConstantSet.KEY_APP_CONFIG_FILE, ConstantSet.KEY_USER_NAME);
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_OLD_PASSWORD, getPwd(userName + oldpassword, 1,
					8)));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_NEW_PASSWORD, getPwd(userName + newpassword, 1,
					8)));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				result.ResultObject = jsonResult.Msg;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		return result;
	}

	public static ActionResult checknickname(String nickname) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_CHECK_NICKNAME);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_NICKNAME, nickname));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					result.ResultObject = jsonResult.getDataString(ServerAPIConstant.KEY_USE);
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		return result;
	}

	public static ActionResult userReg(String userName, String userPassword, String nickname, String sex, String birth) {

		ActionResult result = new ActionResult();
		UserViewModel userInfo = null;
		if (userPassword == null || userPassword.length() == 0) {
			result.ResultCode = BaseActionResult.RESULT_CODE_ERROR;
			result.ResultObject = XhrApplicationBase.CONTEXT.getString(R.string.login_error_tip_account_isnull);
			return result;
		}
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_REG);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_USERNAME, userName));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PASSWORD, getPwd(userName + userPassword, 1, 8)));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_NICKNAME, nickname));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_SEX, sex));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_BIRTH, birth));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_IDS, PackageUtil.getDeviceId()));

		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				if (jsonResult.isOK()) {
					userInfo = new UserViewModel();
					UserInfoModel model = jsonResult.getData(UserInfoModel.class);
					if (!model.getUid().equals(userInfo.UserInfo.getUid())) {
						// 如果用户信息不一致就清除本地信息
						UserMgr.logout();
					}
					userInfo.UserInfo = model;
					result.ResultObject = userInfo;
					userInfo.LoginType = 0;
					result.ResultObject = userInfo;
					UserDao.saveLocalUserInfo(userInfo);
					// 保存用户是否开通小店
					String storestatus = jsonResult.getDataString(ServerAPIConstant.KEY_STORE_STATUS);
					SharedPreferenceUtil.saveValue(XhrApplicationBase.CONTEXT, ServerAPIConstant.KEY_CONFIG_FILENAME,
							ServerAPIConstant.KEY_STORE_STATUS, storestatus);
					String userType = jsonResult.getDataString(ServerAPIConstant.KEY_USERTYPE);
					SharedPreferenceUtil.saveValue(XhrApplicationBase.CONTEXT, ServerAPIConstant.KEY_CONFIG_FILENAME,
							ServerAPIConstant.KEY_USERTYPE, userType);
					// 保存用户分类信息
					List<UserCategoryModel> userCategoryModels = jsonResult.getData(ServerAPIConstant.KEY_CATEGORY,
							new TypeToken<List<UserCategoryModel>>() {
							}.getType());
					DBMgr.deleteTableFromDb(UserCategoryModel.class);
					DBMgr.saveModels(userCategoryModels);
				} else {
					result.ResultObject = jsonResult.Msg;
				}
				result.ResultCode = jsonResult.State;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;

	}

	public static ActionResult userLogin(int logintype, String userName, String userPassword, String openid,
			String accesstoken, String expiredate) {

		ActionResult result = new ActionResult();
		UserViewModel userInfo = null;

		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_LOGIN);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_LOGINTYPE, String.valueOf(logintype)));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_USERNAME, userName));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PASSWORD, getPwd(userName + userPassword, 1, 8)));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_OPENID, openid));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_ACCESSTOKEN, accesstoken));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_EXPIREDATE, expiredate));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_IDS, PackageUtil.getDeviceId()));

		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);

			if (jsonResult != null) {
				if (jsonResult.isOK()) {
					userInfo = getLocalUserInfo();
					UserInfoModel model = jsonResult.getData(UserInfoModel.class);
					if (!model.getUid().equals(userInfo.UserInfo.getUid())) {
						// 如果用户信息不一致就清除本地信息
						UserMgr.logout();
					}
					userInfo.UserInfo = model;
					userInfo.LoginType = logintype;
					userInfo.mWeiboInfoModel.WeiboType = String.valueOf(logintype);
					userInfo.mWeiboInfoModel.WeiboAccount = accesstoken;
					userInfo.mWeiboInfoModel.StatusCode = expiredate;
					UserDao.saveLocalUserInfo(userInfo);
					// 保存用户是否开通小店
					String storestatus = jsonResult.getDataString(ServerAPIConstant.KEY_STORE_STATUS);
					SharedPreferenceUtil.saveValue(XhrApplicationBase.CONTEXT, ServerAPIConstant.KEY_CONFIG_FILENAME,
							ServerAPIConstant.KEY_STORE_STATUS, storestatus);
					String userType = jsonResult.getDataString(ServerAPIConstant.KEY_USERTYPE);
					SharedPreferenceUtil.saveValue(XhrApplicationBase.CONTEXT, ServerAPIConstant.KEY_CONFIG_FILENAME,
							ServerAPIConstant.KEY_USERTYPE, userType);
					// 保存用户分类信息
					List<UserCategoryModel> userCategoryModels = jsonResult.getData(ServerAPIConstant.KEY_CATEGORY,
							new TypeToken<List<UserCategoryModel>>() {
							}.getType());
					DBMgr.deleteTableFromDb(UserCategoryModel.class);
					DBMgr.saveModels(userCategoryModels);
					result.ResultObject = userInfo;
				} else {
					result.ResultObject = jsonResult.Msg;
				}
				result.ResultCode = jsonResult.State;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;
	}

	public static ActionResult userEdit(String nickname, String sex, String birth, String background) {

		ActionResult result = new ActionResult();
		UserViewModel userInfo = null;
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_EDIT);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_NICKNAME, nickname));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_SEX, sex));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_BACKGROUND, background));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_BIRTH, birth));

		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);

			if (jsonResult != null) {
				if (jsonResult.isOK()) {
					String bgUrl = jsonResult.getDataString(ServerAPIConstant.KEY_BACKGROUND);
					userInfo = getLocalUserInfo();
					userInfo.UserInfo.setNickname(nickname);
					userInfo.UserInfo.setBirth(birth);
					if (!StringUtil.isNullOrEmpty(bgUrl)) {
						userInfo.UserInfo.setBackground(bgUrl); // 不为空才保存到本地
					}
					UserDao.saveLocalUserInfo(userInfo);
				} else {
					result.ResultObject = jsonResult.Msg;
				}
				result.ResultCode = jsonResult.State;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;

	}

	public static ActionResult userMyhomeinfo() {

		ActionResult result = new ActionResult();
		UserViewModel userInfo = null;

		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_MYHOMEINFO);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();

		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);

			if (jsonResult != null) {
				EvtLog.d(TAG, " userReg jsonResult: " + jsonResult.JsonString);
				if (jsonResult.isOK()) {
					userInfo = getLocalUserInfo();
					UserInfoModel temp = jsonResult.getData(UserInfoModel.class);
					userInfo.UserInfo.setNickname(temp.getNickname());
					userInfo.UserInfo.setLevel(temp.getLevel());
					userInfo.UserInfo.setIsvideo(temp.getIsvideo());
					userInfo.UserInfo.setSex(temp.getSex());
					userInfo.UserInfo.setBirth(temp.getBirth());
					userInfo.UserInfo.setBackground(temp.getBackground());
					userInfo.UserInfo.setTrendsnum(temp.getTrendsnum());
					userInfo.UserInfo.setFollownum(temp.getFollownum());
					userInfo.UserInfo.setFansnum(temp.getFansnum());
					userInfo.UserInfo.setCoin(temp.getCoin());
					userInfo.UserInfo.setIsnewfans(temp.getIsnewfans());
					userInfo.UserInfo.setNewtrends(temp.getNewtrends());
					userInfo.UserInfo.setHeadimg(temp.getHeadimg());
					UserDao.saveLocalUserInfo(userInfo);
					result.ResultObject = userInfo;
				} else {
					result.ResultObject = jsonResult.Msg;
				}
				result.ResultCode = jsonResult.State;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;

	}

	/**
	 * 查看他人主页
	 * 
	 * @param touid
	 *            他人用户ID
	 * @return 他人数据
	 */
	public static ActionResult otherhomeinfo(String touid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_OTHERHOMEINFO);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TOUID, touid + ""));
		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				if (jsonResult.isOK()) {
					OtherUserModel userInfo = jsonResult.getData(OtherUserModel.class);
					userInfo.setTouid(touid);
					// DBMgr.saveModel(userInfo);
					result.ResultObject = userInfo;
				} else {
					result.ResultObject = jsonResult.Msg;
				}
				result.ResultCode = jsonResult.State;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e.toString());
		}
		return result;
	}

	public static ActionResult getUserBaseInfo(String uid) {
		ActionResult result = new ActionResult();
		UserViewModel userInfo = null;

		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_GETUSERBASEINFO);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_UID, uid));

		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);

			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					userInfo = getLocalUserInfo();
					UserInfoModel temp = jsonResult.getData(UserInfoModel.class);
					userInfo.UserInfo.setNickname(temp.getNickname());
					userInfo.UserInfo.setLevel(temp.getLevel());
					userInfo.UserInfo.setIsvideo(temp.getIsvideo());
					userInfo.UserInfo.setSex(temp.getSex());
					userInfo.UserInfo.setBirth(temp.getBirth());
					userInfo.UserInfo.setBackground(temp.getBackground());
					userInfo.UserInfo.setList(temp.getList());
					UserDao.saveLocalUserInfo(userInfo);
					result.ResultObject = userInfo;
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;

	}

	public static ActionResult photoEdit(String opt, String photoid, String phototype, String photo) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_PHOTOEDIT);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_OPT, String.valueOf(opt)));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PHOTOID, String.valueOf(photoid)));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PHOTOTYPE, String.valueOf(phototype)));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PHOTO, photo));
		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					if ("1".equals(opt)) {
						result.ResultObject = jsonResult.getData(PictureModel.class);
					}
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;

	}

	public static ActionResult setUserHead(String photoid) {

		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_SETUSERHEAD);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PHOTOID, photoid));
		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);

			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				result.ResultObject = jsonResult.Msg;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;
	}

	public static ActionResult getToken() {
		ActionResult result = new ActionResult();
		FileuploadModel mFileuploadInfo = null;
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_FILEUPLOAD_GETTOKEN);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					UserDao.clearLocalFileuploadInfo();
					mFileuploadInfo = jsonResult.getData(FileuploadModel.class);
					result.ResultObject = mFileuploadInfo;
					UserDao.saveLocalFileuploadInfo(mFileuploadInfo);
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			EvtLog.d(TAG, e.toString());
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		return result;

	}

	public static ActionResult userFollow(String type, String followuid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_FOLLOW);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_FOLLOWUID, followuid));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TYPE, type));
		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				result.ResultObject = jsonResult.Msg;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;
	}

	public static ActionResult report(String type, String reportuid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_REPORT);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_REPORTUID, reportuid));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TYPE, type));
		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				result.ResultObject = jsonResult.Msg;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;
	}

	public static ActionResult charge(String paytype, String summoney) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_CHARGE);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PAY_TYPE, paytype));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PAY_SUMMONEY, summoney));
		postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_IDS, PackageUtil.getDeviceId()));
		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					result.ResultObject = jsonResult.getDataString(ServerAPIConstant.KEY_ORDER_INFO);
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			EvtLog.w(TAG, e);
		}
		return result;
	}

	private static String getPwd(String pwd, int start, int end) {
		String temp1 = Md5Tool.md5(pwd);
		String temp2 = temp1.substring(start, end);
		return temp2;
	}

}
