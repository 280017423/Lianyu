package com.xhr88.lp.authentication;

import com.xhr.framework.authentication.BaseActionResult;

/**
 * 动作执行函数
 * 
 * @author zeng.ww
 * @version 1.1.0<br>
 *          2013-03-21，tan.xx，修改继承BaseActionResult
 */
public class ActionResult extends BaseActionResult {

	/**
	 * 网络异常
	 */
	public static final String RESULT_CODE_SUCCESS = "0";
	/**
	 * 网络异常
	 */
	public static final String RESULT_CODE_NET_ERROR = "111";
	/**
	 * token过期状态
	 */
	public static final String RESULT_CODE_NOLOGIN = "4";
}
