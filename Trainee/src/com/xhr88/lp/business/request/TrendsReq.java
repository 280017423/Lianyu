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
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.model.datamodel.CommentModel;
import com.xhr88.lp.model.datamodel.TrendsInfoModel;

/**
 * 动态相关请求类
 * 
 * @author zou.sq
 */
public class TrendsReq {

	public static final String TAG = "TrendsReq";

	/**
	 * 本接口主要提供用户动态详情功能
	 * 
	 * @param tid
	 *            动态ID
	 * @return 动态详情功能
	 */
	public static ActionResult getTrendInfo(String tid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_TRENDS_INFO);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TID, tid));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				if (jsonResult.isOK()) {
					result.ResultObject = jsonResult.getData(TrendsInfoModel.class);
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

	/**
	 * 获取评论列表数据
	 * 
	 * @param tid
	 *            动态ID
	 * @param pindex
	 *            页码
	 * @return 评论列表数据
	 */
	public static ActionResult getCommentLists(String tid, int pindex) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_COMMENT_LISTS);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TID, tid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				if (jsonResult.isOK()) {
					result.ResultObject = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<CommentModel>>() {
							}.getType());
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

	/**
	 * 分享动态
	 * 
	 * @param tid
	 *            分享的动态id
	 * @param platformid
	 *            平台ID，分享的平台id，1=微信好友，2=QQ, 3=新浪，4=邮件，信息，5=QQ空间，6=微信朋友圈
	 * @return 操作返回结果
	 */
	public static ActionResult share(String tid, String platformid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_TRENDS_SHARE);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TID, tid));
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PLATFORMID, platformid));
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

}
