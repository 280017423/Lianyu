package com.xhr.framework.authentication;

/**
 * 动作执行结果封装基类
 * 
 * @author tan.xx
 * @version 2013-3-12 下午2:12:05 tan.xx
 */
public class BaseActionResult {

	public static final String DEFAULT_ERROR_MSG = "error";
	/**
	 * 链接已被释放
	 */
	public static final String RESULT_CODE_IS_RELEASE = "-1";
	/**
	 * 运行成功
	 */
	public static final String RESULT_CODE_SUCCESS = "0";
	/**
	 * 账号未授权
	 */
	public static final String RESULT_CODE_ACCESS_ERROR = "7";
	/**
	 * 运行失败
	 */
	public static final String RESULT_CODE_ERROR = "100";
	/**
	 * 未登录
	 */
	public static final String RESULT_CODE_NOLOGIN = "7";
	/**
	 * 是否进行下一步操作状态
	 */
	public static final String RESULT_CODE_NEXT_ACTION = "4";
	/**
	 * 未知异常
	 */
	public static final String RESULT_CODE_UNKNOW = "5";
	/**
	 * 网络异常
	 */
	public static final String RESULT_STATE_CODE_NET_ERROR = "100";
	/**
	 * 本地参数错误
	 */
	public static final String RESULT_STATE_CODE_PARAM_ERROR = "2";
	/**
	 * 服务器返回参数异常
	 */
	public static final String RESULT_STATE_CODE_RETURN_STATE_ERROR = "1";

	/**
	 * 结果状态
	 */
	public String ResultCode = "0";
	/**
	 * 结果状态码
	 */
	public String ResultStateCode;
	/**
	 * 结果对象
	 */
	public Object ResultObject;

	/**
	 * 动作执行结果
	 */
	public BaseActionResult() {
	}
}
