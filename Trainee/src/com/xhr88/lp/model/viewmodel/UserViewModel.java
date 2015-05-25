/**
 * @Project: PMH
 * @Title: UserViewModel.java
 * @Package com.pdw.pmh.model.viewmodel
 * @Description: 用户信息模块
 * @author zeng.ww
 * @date 2012-8-15 上午11:08:58
 * @Copyright: 2012 www.paidui.cn Inc. All rights reserved.
 * @version V1.0
 */
package com.xhr88.lp.model.viewmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.xhr88.lp.model.datamodel.UserInfoModel;
import com.xhr88.lp.model.datamodel.WeiboInfoModel;

/**
 * 用户信息模块;用于操作用户信息Preference；
 * 
 */
public class UserViewModel implements Serializable {

	private static final long serialVersionUID = 354246406556856136L;

	public static final String SHAREPREFERENCES_NAME = "com.xhr88.lp.user";
	public static final int MAN_CODE = 1;
	public static final int WOMAN_CODE = 0;
	public static final int NOT_SET_SEX_CODE = 2;
	public static final int NOT_SAVE_SEX_CODE = -1;
	/**
	 * 用户信息
	 */
	public UserInfoModel UserInfo = new UserInfoModel();

	/**
	 * 微博绑定状态
	 */
	public WeiboInfoModel mWeiboInfoModel = new WeiboInfoModel();

	/**
	 * 用户登录类型 0： 平台用户 1：微信用户 2：QQ用户3:新浪用户---//
	 * 这里一定要复制初始值，不然默认为null，在保存到SharePreference之后
	 * ，会为null，导致在第一次获取为null，跟其它数值比较时，会报错；
	 */
	public Integer LoginType = -1;

	/**
	 * 用户相册
	 */
	public List<String> list = new ArrayList<String>();
}
