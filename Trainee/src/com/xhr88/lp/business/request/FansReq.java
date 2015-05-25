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
import com.xhr88.lp.model.datamodel.AttentionModel;
import com.xhr88.lp.model.datamodel.FansModel;

public class FansReq {

	public static ActionResult getFansList(final int pindex) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_FANSLIST);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TYPE, "1"));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<FansModel> fansList = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<FansModel>>() {
							}.getType());
					if (null != fansList) {
						if (0 == pindex) {
							DBMgr.deleteTableFromDb(FansModel.class);
						}
						DBMgr.saveModels(fansList);
					}
					result.ResultObject = fansList;
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
			result.ResultObject = DBMgr.getBaseModel(FansModel.class);
		}
		return result;
	}

	public static ActionResult getAttentionList(final int pindex) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_USER_FANSLIST);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TYPE, "2"));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<AttentionModel> attentionsList = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<AttentionModel>>() {
							}.getType());
					if (null != attentionsList) {
						if (0 == pindex) {
							DBMgr.deleteTableFromDb(AttentionModel.class);
						}
						DBMgr.saveModels(attentionsList);
					}
					result.ResultObject = attentionsList;
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
			result.ResultObject = DBMgr.getBaseModel(AttentionModel.class);
		}
		return result;
	}

}
