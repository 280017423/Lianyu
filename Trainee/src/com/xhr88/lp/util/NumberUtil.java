package com.xhr88.lp.util;

/**
 * 数字工具类
 * 
 * @author Wang.xy
 * @since 2013-03-28 zeng.ww 添加SpecialDishModel表初始化操作
 */
public class NumberUtil {

	private static final int TRENDS_NUMBER_UNIT = 10000;

	/**
	 * 格式化数字
	 * 
	 * @param number
	 * @return
	 */
	public static String format(int number) {
		if (number <= 0) {
			return "0";
		}
		String result = "";
		int temp = number / TRENDS_NUMBER_UNIT;
		if (temp > 0) {
			if (number % TRENDS_NUMBER_UNIT >= TRENDS_NUMBER_UNIT / 2) {
				result = (temp + 1) + "万";
			} else {
				result = temp + "万";
			}
		} else {
			result = "" + number;
		}
		return result;
	}

}
