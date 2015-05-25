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
import com.xhr88.lp.model.datamodel.HistoryPayModel;
import com.xhr88.lp.model.viewmodel.MyCoinModel;

public class TaskReq {

	public static final String TAG = "TaskReq";

	/**
	 * 获取任务列表
	 * 
	 * @return 任务列表数据
	 */
	public static ActionResult getTaskList() {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_TASK_LIST);
		try {
			JsonResult jsonResult = HttpClientUtil.post(url, null, null);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					MyCoinModel helpList = jsonResult.getData(new TypeToken<MyCoinModel>() {
					}.getType());
					result.ResultObject = helpList;
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

	/**
	 * 获取任务列表
	 * 
	 * @return 任务列表数据
	 */
	public static ActionResult getHistoryList(int pindex) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_TASK_HISTORY);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_PINDEX, pindex + ""));
			JsonResult jsonResult = HttpClientUtil.post(url, null, postParams);
			if (jsonResult != null) {
				result.ResultCode = jsonResult.State;
				if (jsonResult.isOK()) {
					List<HistoryPayModel> helpList = jsonResult.getData(ServerAPIConstant.KEY_LIST,
							new TypeToken<List<HistoryPayModel>>() {
							}.getType());
					result.ResultObject = helpList;
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

	/**
	 * 本接口主要提领取任务
	 * 
	 * @param taskid
	 *            任务ID
	 * @return 领取结果
	 */
	public static ActionResult receiveTask(String taskid) {
		ActionResult result = new ActionResult();
		String url = ServerAPIConstant.getAPIUrl(ServerAPIConstant.ACTION_TASK_RECEIVE);
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(ServerAPIConstant.KEY_TASKID, taskid));
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
