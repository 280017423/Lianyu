package com.xhr88.lp.business.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.pdw.gson.reflect.TypeToken;
import com.xhr.framework.app.JsonResult;
import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.HttpClientUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.model.datamodel.BuyServiceModel;
import com.xhr88.lp.model.datamodel.HistoryServiceModel;
import com.xhr88.lp.model.datamodel.ShopInfoModel;
import com.xhr88.lp.util.SharedPreferenceUtil;

/**
 * 我的小店请求类
 * 
 * @author zou.sq
 */
public class LittleShopReq {

	private static final String TAG = "LittleShopReq";

	/**
	 * 申请小店
	 * 
	 * @return 申请结果
	 */
	public static ActionResult applyShop(String phone, String qq) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_APPLY_SHOP);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PHONE, phone));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_QQ, qq));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				result.ResultObject = jsonResult.Msg;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			EvtLog.d(TAG, e.getMessage().toString());
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		return result;
	}

	/**
	 * 本接口主要提供小店信息
	 * 
	 * @return 小店信息
	 */
	public static ActionResult getShopInfo() {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_SHOP_INFO);
		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, null);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					result.ResultObject = jsonResult.getData(ShopInfoModel.class);
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			EvtLog.d(TAG, e.getMessage().toString());
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		return result;
	}

	/**
	 * 本接口主要提供小店信息编辑
	 * 
	 * @param description
	 *            小店描述
	 * @param status
	 *            状态
	 * @param servenum
	 *            人数
	 * @return 小店信息编辑结果
	 */
	public static ActionResult editShopInfo(String description, String status, String servenum, String usertype) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_SHOP_EDIT);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			if (!StringUtil.isNullOrEmpty(description)) {
				postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_DESCRIPTION, description));
			}
			if (!StringUtil.isNullOrEmpty(status)) {
				postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_STATUS, status));
			}
			if (!StringUtil.isNullOrEmpty(servenum)) {
				postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_SERVENUM, servenum));
			}
			if (!StringUtil.isNullOrEmpty(usertype)) {
				postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_USERTYPE, usertype));
			}
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				result.ResultObject = jsonResult.Msg;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			EvtLog.d(TAG, e.getMessage().toString());
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		return result;
	}

	/**
	 * 本接口主要提供小店服务编辑
	 * 
	 * @param sid
	 *            服务ID
	 * @param status
	 *            1=打开，0=关闭
	 * @return 服务编辑结果信息
	 */
	public static ActionResult editServe(String sid, String status) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_SERVE_EDIT);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_SID, sid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_OPEN, status));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				result.ResultObject = jsonResult.Msg;
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			EvtLog.d(TAG, e.getMessage().toString());
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		return result;
	}

	public static ActionResult getHistoryService(String type, int pindex) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_SERVE_HISTORY);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TYPE, type + ""));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<HistoryServiceModel> helpList = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<HistoryServiceModel>>() {
							}.getType());
					if (null != helpList) {
						if (0 == pindex) {
							DBMgr.deleteTableFromDb(HistoryServiceModel.class);
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
			result.ResultObject = DBMgr.getBaseModel(HistoryServiceModel.class);
		}
		return result;
	}

	public static ActionResult getBuyService(String touid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_SERVE_LIST);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TOUID, touid));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					result.ResultObject = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<BuyServiceModel>>() {
							}.getType());
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

	public static ActionResult buyService(String buyuid, String sid, String num) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_SERVE_BUY);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_BUY_UID, buyuid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_SID, sid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_NUM, num));
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

	public static ActionResult getapplystatus() {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_GET_APPLY_STATUS);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				if (jsonResult.isOK()) {
					// 保存用户是否开通小店
					String storestatus = jsonResult.getDataString(ServerAPIConstant.KEY_STORE_STATUS);
					SharedPreferenceUtil.saveValue(XhrApplicationBase.CONTEXT, ServerAPIConstant.KEY_CONFIG_FILENAME,
							ServerAPIConstant.KEY_STORE_STATUS, storestatus);
					result.ResultObject = jsonResult.getDataString(ServerAPIConstant.KEY_INFO);
				} else {
					result.ResultObject = jsonResult.Msg;
				}
				result.ResultCode = jsonResult.State;
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
