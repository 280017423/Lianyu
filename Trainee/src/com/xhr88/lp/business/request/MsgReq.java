package com.xhr88.lp.business.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.pdw.gson.reflect.TypeToken;
import com.xhr.framework.app.JsonResult;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.HttpClientUtil;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.model.datamodel.ConversationUserInfoModel;
import com.xhr88.lp.model.datamodel.ServiceModel;
import com.xhr88.lp.model.viewmodel.UserViewModel;

public class MsgReq {
	private static final String TAG = "MsgReq";

	public static ActionResult getToken() {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_MSG_GET_TOKEN);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					String token = jsonResult.getDataString(ServerAPIConstant.KEY_IMTOKEN);
					if (UserMgr.hasUserInfo()) {
						UserViewModel userInfo = UserDao.getLocalUserInfo();
						userInfo.UserInfo.setImtoken(token);
						UserDao.saveLocalUserInfo(userInfo);
					}
					result.ResultObject = token;
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

	public static ActionResult getUserInfo(String uids) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_MSG_GET_USER_INFO);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_UIDS, uids));
			JsonResult jsonResult = HttpClientUtil.post(url, HttpClientUtil.NORMAL_REQUEST, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<ConversationUserInfoModel> models = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<ConversationUserInfoModel>>() {
							}.getType());
					if (null != models && !models.isEmpty()) {
						result.ResultObject = models.get(0);
						DBMgr.saveModel(models.get(0), "UID = ?", uids);
					}
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

	public static ActionResult numSearch(String uids) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_SERVICE_SEARCH);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_BUY_UID, uids));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					ServiceModel model = jsonResult.getData(ServiceModel.class);
					result.ResultObject = model;
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

	public static ActionResult serviceComment(String bid, String rank) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_COMMENT);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_BID, bid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_RANK, rank));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				result.ResultObject = jsonResult.Msg;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			EvtLog.d(TAG, e.toString());
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		return result;
	}
}
