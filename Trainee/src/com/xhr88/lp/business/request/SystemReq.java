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
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.model.datamodel.ConfigModel;
import com.xhr88.lp.model.datamodel.HelpListModel;
import com.xhr88.lp.model.datamodel.UpdateModel;
import com.xhr88.lp.model.datamodel.UserCategoryModel;

public class SystemReq {

	public static final String TAG = "HelpReq";

	/**
	 * 获取帮助列表
	 * 
	 * @param pindex
	 *            页数
	 * @return 帮助列表数据
	 */
	public static ActionResult getHelpList(int pindex) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_HELP_LIST);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<HelpListModel> helpList = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<HelpListModel>>() {
							}.getType());
					if (null != helpList) {
						if (0 == pindex) {
							DBMgr.deleteTableFromDb(HelpListModel.class);
						}
						DBMgr.saveModels(helpList);
					}
					result.ResultObject = helpList;
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		// 说明当前列表没有数据，此时显示缓存数据
		if (result.ResultCode.equals(ActionResult.RESULT_CODE_NET_ERROR)) {
			result.ResultObject = DBMgr.getBaseModel(HelpListModel.class);
		}
		return result;
	}

	/**
	 * 用户反馈
	 * 
	 * @param type
	 *            反馈类型 1=投诉，2=建议
	 * @param complainuid
	 *            被投诉方uid，类型为建议时，可为空
	 * @param content
	 *            反馈内容
	 * @param imgurl
	 *            图片地址，多张以 , 符号分隔
	 * @return 帮助列表数据
	 */
	public static ActionResult feedBack(String type, String complainuid, String content, String imgurl) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_FEED_BACK);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TYPE, type));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_COMPLAINUID, complainuid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_CONTENT, content));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_IMGURL, imgurl));
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

	public static ActionResult start() {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_START);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					// 保存用户配置信息
					ConfigModel configModel = jsonResult.getData(ConfigModel.class);
					DBMgr.deleteTableFromDb(ConfigModel.class);
					DBMgr.saveModel(configModel);

					// 保存用户分类信息
					List<UserCategoryModel> userCategoryModels = jsonResult.getData(ServerAPIConstant.KEY_CATEGORY,
							new TypeToken<List<UserCategoryModel>>() {
							}.getType());
					DBMgr.deleteTableFromDb(UserCategoryModel.class);
					DBMgr.saveModels(userCategoryModels);
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

	public static ActionResult checkUpdate() {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_CHECK_UPDATE);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					result.ResultObject = jsonResult.getData(UpdateModel.class);
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

}
