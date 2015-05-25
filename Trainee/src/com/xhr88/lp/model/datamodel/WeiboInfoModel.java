package com.xhr88.lp.model.datamodel;

import java.io.Serializable;

/**
 * 微博信息类，用于操作 SharePreference
 * 
 * @author
 * @version
 */
public class WeiboInfoModel implements Serializable {

	private static final long serialVersionUID = 3569805345220664952L;
	/**
	 * 微博类型; 一定要优先初始华
	 */
	public String WeiboType;
	/**
	 * 微博账号
	 */
	public String WeiboAccount;
	/**
	 * 绑定状态
	 */
	public String StatusCode;

}
