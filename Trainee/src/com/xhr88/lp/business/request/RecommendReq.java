package com.xhr88.lp.business.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.pdw.gson.reflect.TypeToken;
import com.xhr.framework.app.JsonResult;
import com.xhr.framework.util.HttpClientUtil;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.model.datamodel.RecommendListModel;
import com.xhr88.lp.model.datamodel.SearchUserModel;

/**
 * 用户请求类
 * 
 * @author zou.sq
 */
public class RecommendReq {

	public static ActionResult getRecommendList(String type, String categoryname, final int pindex) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_RECOMMEND_LISTS);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TYPE, type));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_CATEGORY_NAME, categoryname));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<RecommendListModel> albumList = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<RecommendListModel>>() {
							}.getType());
					if (null != albumList) {
						if (0 == pindex) {
							DBMgr.deleteTableFromDb(RecommendListModel.class);
						}
						DBMgr.saveModels(albumList);
					}
					result.ResultObject = albumList;
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
			result.ResultObject = DBMgr.getBaseModel(RecommendListModel.class);
		}
		return result;
	}

	/**
	 * 用户搜索
	 * 
	 * @return 用户信息
	 */
	public static ActionResult searchUser(String keyword, int pindex) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_SEARCH);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_KEYWORD, keyword));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<SearchUserModel> searchUserModels = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<SearchUserModel>>() {
							}.getType());
					result.ResultObject = searchUserModels;
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

}
