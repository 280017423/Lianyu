package com.xhr88.lp.common;

import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.util.AppUtil;
import com.xhr.framework.util.EvtLog;

public class ServerAPIConstant {
	public static final String API_ROOT_URL = "api_root_url";

	// Action字段
	public static final String ACTION_EDIT_PASSWORD = "/user/editpassword";
	public static final String ACTION_CHECK_NICKNAME = "/user/checknickname";
	public static final String ACTION_USER_REG = "/user/reg";
	public static final String ACTION_USER_LOGIN = "/user/login";
	public static final String ACTION_USER_EDIT = "/user/edit";
	public static final String ACTION_USER_MYHOMEINFO = "/user/myhomeinfo";
	public static final String ACTION_USER_GETUSERBASEINFO = "/user/getuserbaseinfo";
	public static final String ACTION_USER_FOLLOW = "/user/follow";
	public static final String ACTION_USER_REPORT = "/user/report";
	public static final String ACTION_USER_OTHERHOMEINFO = "/user/otherhomeinfo";
	public static final String ACTION_USER_PHOTOEDIT = "/user/photoedit";
	public static final String ACTION_USER_SETUSERHEAD = "/user/setuserhead";
	public static final String ACTION_USER_FANSLIST = "/user/fanslist";
	public static final String ACTION_USER_COORDINATE = "/user/coordinate";

	public static final String ACTION_RECOMMEND_LISTS = "/recommend/lists";
	public static final String ACTION_USER_CATEGORY = "/recommend/usercategory";
	public static final String ACTION_USER_SEARCH = "/search/user";

	// 小店接口
	public static final String ACTION_APPLY_SHOP = "/userstore/apply";
	public static final String ACTION_SHOP_INFO = "/userstore/info";
	public static final String ACTION_SHOP_EDIT = "/userstore/edit";
	public static final String ACTION_SERVE_EDIT = "/userstore/serveedit";
	public static final String ACTION_SERVE_HISTORY = "/userstore/history";
	public static final String ACTION_SERVE_LIST = "/userstore/serverlist";
	public static final String ACTION_SERVE_BUY = "/userstore/buy";
	public static final String ACTION_COMMENT = "/userstore/comment";
	public static final String ACTION_GET_APPLY_STATUS = "/userstore/getapplystatus";

	public static final String ACTION_TRENDS_ADD = "/trends/add";
	public static final String ACTION_TRENDS_SHARE = "/trends/share";
	public static final String ACTION_TRENDS_SHAREURL = "/trends/shareurl";
	public static final String ACTION_TRENDS_LIKE = "/trends/like";
	public static final String ACTION_ATTENTION_TRENDS_LISTS = "/trends/myfollowlists";
	public static final String ACTION_OTHER_TRENDS_LISTS = "/trends/otherlists";
	public static final String ACTION_MY_TRENDS_LISTS = "/trends/mylists";
	public static final String ACTION_TRENDS_INFO = "/trends/info";
	public static final String ACTION_TRENDS_DEL = "/trends/del";

	public static final String ACTION_COMMENT_ADD = "/comment/add";
	public static final String ACTION_COMMENT_LISTS = "/comment/lists";
	public static final String ACTION_COMMENT_DEL = "/comment/del";

	public static final String ACTION_FILEUPLOAD_GETTOKEN = "/fileupload/gettoken";

	public static final String ACTION_HELP_LIST = "/system/helplist";
	public static final String ACTION_FEED_BACK = "/system/feedback";
	public static final String ACTION_CHECK_UPDATE = "/system/checkupdate";
	public static final String ACTION_START = "/system/start";

	public static final String ACTION_TASK_LIST = "/task/lists";
	public static final String ACTION_TASK_HISTORY = "/pay/lplist";
	public static final String ACTION_CHARGE = "/pay/recharge";
	public static final String ACTION_TASK_RECEIVE = "/task/receive";

	public static final String ACTION_MSG_GET_TOKEN = "/msg/gettoken";
	public static final String ACTION_MSG_GET_USER_INFO = "/msg/getuserinfo";

	public static final String ACTION_SERVICE_SEARCH = "/userserve/search";
	// KEY字段
	public static final String KEY_USERNAME = "username";
	public static final String KEY_OLD_PASSWORD = "oldpassword";
	public static final String KEY_NEW_PASSWORD = "newpassword";
	public static final String KEY_USE = "use";

	public static final String KEY_UID = "uid";
	public static final String KEY_UIDS = "uids";
	public static final String KEY_TOKEN = "token";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_LOGINTYPE = "logintype";
	public static final String KEY_OPENID = "openid";
	public static final String KEY_ACCESSTOKEN = "accesstoken";
	public static final String KEY_EXPIREDATE = "expiredate";
	public static final String KEY_IDS = "ids";
	public static final String KEY_NICKNAME = "nickname";
	public static final String KEY_SEX = "sex";
	public static final String KEY_BIRTH = "birth";
	public static final String KEY_BID = "bid";
	public static final String KEY_RANK = "rank";
	public static final String KEY_BACKGROUND = "background";
	public static final String KEY_OPT = "opt";
	public static final String KEY_PHOTO = "photo";
	public static final String KEY_TOUID = "touid";
	public static final String KEY_PHOTOID = "photoid";
	public static final String KEY_PHOTOTYPE = "phototype";
	public static final String KEY_FOLLOWUID = "followuid";
	public static final String KEY_REPORTUID = "reportuid";
	public static final String KEY_RELATION = "relation";
	public static final String KEY_BUY_UID = "buyuid";
	public static final String KEY_ORDER_INFO = "orderinfo";

	public static final String KEY_LNG = "lng";
	public static final String KEY_LAT = "lat";
	public static final String KEY_TRENDSTYPE = "trendstype";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_RESOURCE = "resource";
	public static final String KEY_TID = "tid";
	public static final String KEY_PLATFORMID = "platformid";
	public static final String KEY_COMUID = "comuid";
	public static final String KEY_MAXID = "maxid";
	public static final String KEY_CID = "cid";
	public static final String KEY_TASKID = "taskid";
	public static final String KEY_IMTOKEN = "imtoken";
	public static final String KEY_PAY_TYPE = "paytype";
	public static final String KEY_PAY_SUMMONEY = "summoney";

	public static final String KEY_TYPE = "type";
	public static final String KEY_IMGURL = "imgurl";
	public static final String KEY_COMPLAINUID = "complainuid";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_SID = "sid";
	public static final String KEY_NUM = "num";
	public static final String KEY_STATUS = "status";
	public static final String KEY_SERVENUM = "servenum";
	public static final String KEY_USERTYPE = "usertype";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_STORE_STATUS = "storestatus";
	public static final String KEY_CATEGORY_NAME = "categoryname";
	public static final String KEY_PINDEX = "pindex";
	public static final String KEY_PAGE = "page";
	public static final String KEY_LIST = "list";
	public static final String KEY_KEYWORD = "keyword";
	public static final String KEY_OPEN = "open";
	public static final String KEY_INFO = "info";
	public static final String KEY_PHONE = "phone";
	public static final String KEY_QQ = "qq";

	// 配置信息保存文件，登出时删除
	public static final String KEY_CONFIG_FILENAME = "KEY_CONFIG_FILENAME";

	/**
	 * 获取后端的 api URL地址
	 * 
	 * @param actions
	 *            方法的子路径
	 * @return 返回后端的 api URL地址
	 */
	public static String getAPIUrl(String actions) {
		String url = getApiRootUrl() + actions;
		EvtLog.d("ServerAPIConstant: ", url);
		return url;
	}

	/**
	 * 获取接口地址
	 * 
	 * @return String
	 * @throws
	 */
	private static String getApiRootUrl() {
		return AppUtil.getMetaDataByKey(XhrApplicationBase.CONTEXT, API_ROOT_URL);
	}

	/**
	 * 获取活动地址
	 * 
	 * @return String
	 * @throws
	 */
	public static String getActivityUrl() {
		return AppUtil.getMetaDataByKey(XhrApplicationBase.CONTEXT, "activity_url");
	}
}
