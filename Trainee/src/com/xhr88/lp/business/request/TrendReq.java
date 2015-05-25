package com.xhr88.lp.business.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.pdw.gson.reflect.TypeToken;
import com.xhr.framework.app.JsonResult;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.HttpClientUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.business.dao.TrendsDao;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.model.datamodel.AttentionTrendsModel;
import com.xhr88.lp.model.datamodel.CommentModel;
import com.xhr88.lp.model.datamodel.OtherTrendsModel;
import com.xhr88.lp.model.datamodel.TrendsListModel;

public class TrendReq {

	public static final String TAG = "TrendReq";

	public static ActionResult getOtherTrendsList(final String uid, final int pindex) {
		ActionResult result = new ActionResult();
		String maxid;

		OtherTrendsModel tempModel = TrendsDao.getHistoryData(uid);
		if (tempModel == null) {
			maxid = "0";
		} else {
			maxid = tempModel.getMaxid();
		}
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_OTHER_TRENDS_LISTS);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_UID, uid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_MAXID, maxid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					OtherTrendsModel otherTrendsModel = jsonResult.getData(OtherTrendsModel.class);
					if (null != otherTrendsModel) {
						if (0 == pindex) {
							otherTrendsModel.setUid(uid);
							DBMgr.deleteTableFromDb(OtherTrendsModel.class);
							DBMgr.saveModel(otherTrendsModel);
						} else {
							// // TODO 根据uid拿到本地记录，更新动态部分然后在存储
							// OtherTrendsModel model =
							// TrendsDao.getHistoryData(uid);
							// model.setList(otherTrendsModel.getList());
						}
					}
					result.ResultObject = otherTrendsModel;
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			EvtLog.w(TAG, e.toString());
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		// 说明当前列表没有数据，此时显示缓存数据
		if (result.ResultCode.equals(ActionResult.RESULT_CODE_NET_ERROR)) {
			result.ResultObject = TrendsDao.getHistoryData(uid);
		}
		return result;
	}

	public static ActionResult getAttentionTrendsList(final int pindex) {
		ActionResult result = new ActionResult();
		String maxid;

		List<AttentionTrendsModel> mList = DBMgr.getHistoryData(AttentionTrendsModel.class);
		if (mList == null || mList.isEmpty()) {
			maxid = "0";
		} else {
			maxid = mList.get(0).getTid();
		}
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_ATTENTION_TRENDS_LISTS);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_MAXID, maxid + ""));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<AttentionTrendsModel> dynamicList = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<AttentionTrendsModel>>() {
							}.getType());
					if (null != dynamicList) {
						if (0 == pindex) {
							DBMgr.deleteTableFromDb(AttentionTrendsModel.class);
						}
						DBMgr.saveModels(dynamicList);
					}
					result.ResultObject = dynamicList;
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			EvtLog.w(TAG, e.toString());
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		// 说明当前列表没有数据，此时显示缓存数据
		if (result.ResultCode.equals(ActionResult.RESULT_CODE_NET_ERROR)) {
			result.ResultObject = DBMgr.getBaseModel(AttentionTrendsModel.class);
		}
		return result;
	}

	public static ActionResult getMyTrendsList(final int pindex) {
		ActionResult result = new ActionResult();
		String maxid;

		List<TrendsListModel> mList = DBMgr.getHistoryData(TrendsListModel.class);
		if (mList == null || mList.isEmpty()) {
			maxid = "0";
		} else {
			maxid = mList.get(0).getTid();
		}
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_MY_TRENDS_LISTS);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_MAXID, maxid + ""));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<TrendsListModel> dynamicList = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<TrendsListModel>>() {
							}.getType());
					if (null != dynamicList) {
						if (0 == pindex) {
							DBMgr.deleteTableFromDb(TrendsListModel.class);
						}
						DBMgr.saveModels(dynamicList);
					}
					result.ResultObject = dynamicList;
				} else {
					result.ResultObject = jsonResult.Msg;
				}
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			EvtLog.w(TAG, e.toString());
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}
		// 说明当前列表没有数据，此时显示缓存数据
		if (result.ResultCode.equals(ActionResult.RESULT_CODE_NET_ERROR)) {
			result.ResultObject = DBMgr.getBaseModel(TrendsListModel.class);
		}
		return result;
	}

	public static ActionResult trendsAdd(int trendstype, String content, String resource, String lng, String lat) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_TRENDS_ADD);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TRENDSTYPE, trendstype + ""));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_CONTENT, content));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_RESOURCE, resource));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_LNG, lng));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_LAT, lat));
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

	/**
	 * 喜欢动态
	 * 
	 * @param tid
	 *            分享的动态id
	 * @param opt
	 *            操作方式，1=喜欢 ,2=取消喜欢（暂时不做取消喜欢）
	 * @return 操作返回结果
	 */
	public static ActionResult trendsLike(String tid, String opt) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_TRENDS_LIKE);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TID, tid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_OPT, opt));
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

	public static ActionResult trendsDel(String tid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_TRENDS_DEL);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TID, tid));
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

	public static ActionResult commentDel(String cid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_COMMENT_DEL);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_CID, cid));
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

	public static ActionResult commentAdd(String tid, String content, String comuid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_COMMENT_ADD);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TID, tid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_CONTENT, content));
			if (!StringUtil.isNullOrEmpty(comuid)) {
				postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_COMUID, comuid + ""));
			}
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				result.ResultObject = jsonResult.getData(CommentModel.class);
			} else {
				result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
			}
		} catch (Exception e) {
			result.ResultCode = ActionResult.RESULT_CODE_NET_ERROR;
		}

		return result;
	}

}
