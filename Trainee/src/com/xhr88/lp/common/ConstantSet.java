package com.xhr88.lp.common;

import com.xhr88.lp.R;

public class ConstantSet {
	// 项目名称
	public static final String APP_SIGN = "xhr";

	public static final String DEFAULT_ERROR_MSG = "error";
	public static final String DEFAULT_NULL_MSG = "[]";
	// 数据库操作常量
	public static final String EQUAl = " = ";
	public static final String EQUALS = " = ?";
	public static final String AND = " and ";
	public static final String QUOTES = "\"";
	public static final String AT = "@";
	public static final int INVALIDITY = -1;
	// APP config file,登出不删除的
	public static final String KEY_APP_CONFIG_FILE = "KEY_APP_CONFIG_FILE";
	public static final String KEY_USER_NAME = "KEY_USER_NAME";
	// 新手引导逻辑常量
	public static final String KEY_NEWER_GUIDING_FILE = "KEY_NEWER_GUIDING_FILE";
	public static final String KEY_NEWER_GUIDING_FINISH = "KEY_NEWER_GUIDING_FINISH";
	// 默认每页条数
	public static final int PAGE_SIZE = 20;
	public static final int COMMENTS_PAGE_SIZE = 100;
	// 背景大小
	public static final int BG_WIDTH = 720;
	public static final int BG_HEIFHT = 720;

	// 缩略图大小尺寸
	public static final int THUMBNAIL_WIDTH = 162;
	public static final int THUMBNAIL_HEIFHT = 162;

	// 跳转常量键
	public static final String KEY_TID = "KEY_TID";
	public static final String KEY_INTRODUCE_CONTENT = "KEY_INTRODUCE_CONTENT";

	public static final String EXTRA_IMAGE_ITEM_MODEL = "EXTRA_IMAGE_ITEM_MODEL";
	public static final String EXTRA_IMAGE_CHOOSE_LIST = "EXTRA_IMAGE_CHOOSE_LIST";
	public static final String EXTRA_IMAGE_CHOOSE_IS_COMPLETED = "EXTRA_IMAGE_CHOOSE_IS_COMPLETED";
	public static final String EXTRA_IMAGE_LIST = "EXTRA_IMAGE_LIST";
	public static final String ADD_IMAGE_MAX_ITEM = "ADD_IMAGE_MAX_ITEM";
	public static final String ONLINE_IMAGE_DETAIL = "ONLINE_IMAGE_DETAIL";
	public static final String ONLINE_IMAGE_SMALL = "ONLINE_IMAGE_SMALL";
	public static final String EXIT_TO_CANCEL_APK = "EXIT_TO_CANCEL_APK";

	public static final String ACTION_REFREASH_COIN = "ACTION_REFREASH_COIN";

	public static final String ACTION_REGISTER_SUCCESS = "ACTION_REGISTER_SUCCESS";
	public static final String ACTION_LOGIN_SUCCESS = "ACTION_LOGIN_SUCCESS";
	public static final int[] ICONS_LEVEL = new int[] {
			R.drawable.icon_level_1,
			R.drawable.icon_level_2,
			R.drawable.icon_level_3,
			R.drawable.icon_level_4,
			R.drawable.icon_level_5,
			R.drawable.icon_level_6,
			R.drawable.icon_level_7,
			R.drawable.icon_level_8,
			R.drawable.icon_level_9,
			R.drawable.icon_level_10,
			R.drawable.icon_level_11,
			R.drawable.icon_level_12,
			R.drawable.icon_level_13,
			R.drawable.icon_level_14,
			R.drawable.icon_level_15 };

	public static int getLevelIcons(int level) {
		try {
			return ICONS_LEVEL[level - 1];
		} catch (Exception e) {
		}
		return ICONS_LEVEL[0];
	}

}
