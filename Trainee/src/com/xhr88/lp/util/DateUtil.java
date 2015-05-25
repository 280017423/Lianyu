package com.xhr88.lp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 日期工具类
 * 
 * @author Wang.xy
 * @since 2013-03-28 zeng.ww 添加SpecialDishModel表初始化操作
 */
public class DateUtil {

	/**
	 * 
	 * @param time
	 *            秒为单位
	 * @return
	 */
	public static String formatDateTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return formatDateTime(format.format(new Date(time * 1000)));
	}

	/**
	 * 
	 * @param time
	 *            秒为单位
	 * @return
	 */
	public static String formatTrendsDate(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return formatTrendsDate(format.format(new Date(time * 1000)));
	}

	/**
	 * 
	 * @param time
	 *            秒为单位
	 * @return
	 */
	public static String getMonth(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return getMonth(format.format(new Date(time * 1000)));
	}

	public static String getMonth(String time) {
		String[] months = new String[] { "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月", };
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar current = Calendar.getInstance();
		current.setTime(date);
		return months[current.get(Calendar.MONTH)];
	}

	/**
	 * 格式化时间
	 * 
	 * @param time
	 * @return
	 */
	public static String formatDateTime(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar current = Calendar.getInstance();

		Calendar today = Calendar.getInstance(); // 今天

		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		Calendar yesterday = Calendar.getInstance(); // 昨天

		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);

		Calendar dayBeforeYesterday = Calendar.getInstance(); // 昨天

		dayBeforeYesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		dayBeforeYesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		dayBeforeYesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 2);
		dayBeforeYesterday.set(Calendar.HOUR_OF_DAY, 0);
		dayBeforeYesterday.set(Calendar.MINUTE, 0);
		dayBeforeYesterday.set(Calendar.SECOND, 0);

		current.setTime(date);
		if (isSameDay(current, today)) {
			return "今天";
		} else if (isSameDay(current, yesterday)) {
			return "昨天";
		} else if (isSameDay(current, dayBeforeYesterday)) {
			return "前天";
		} else if (current.before(dayBeforeYesterday)) {
			return time;
		}
		return "";
	}

	public static String formatTrendsDate(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar current = Calendar.getInstance();
		Calendar today = Calendar.getInstance(); // 今天
		Calendar yesterday = Calendar.getInstance(); // 昨天
		Calendar dayBeforeYesterday = Calendar.getInstance(); // 昨天

		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);

		dayBeforeYesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		dayBeforeYesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		dayBeforeYesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 2);
		dayBeforeYesterday.set(Calendar.HOUR_OF_DAY, 0);
		dayBeforeYesterday.set(Calendar.MINUTE, 0);
		dayBeforeYesterday.set(Calendar.SECOND, 0);

		current.setTime(date);
		if (isSameDay(current, today)) {
			return "今天";
		} else if (isSameDay(current, yesterday)) {
			return "昨天";
		} else if (isSameDay(current, dayBeforeYesterday)) {
			return "前天";
		} else if (current.before(dayBeforeYesterday)) {
			return current.get(Calendar.DAY_OF_MONTH) + "";
		}
		return "";
	}

	private static boolean isSameDay(Calendar current, Calendar compareCalendar) {
		return current.get(Calendar.YEAR) == compareCalendar.get(Calendar.YEAR)
				&& current.get(Calendar.MONTH) == compareCalendar.get(Calendar.MONTH)
				&& current.get(Calendar.DAY_OF_MONTH) == compareCalendar.get(Calendar.DAY_OF_MONTH);
	}

	public static String getQiniuKey(String subject, String key) {

		return subject + "/" + com.xhr.framework.util.DateUtil.getSysDate("yyyyMMdd") + "/" + key;
	}

	public static int getAgeByDate(Date birthDay) throws Exception {
		Calendar cal = Calendar.getInstance();
		if (cal.before(birthDay)) {
			throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
		}
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				age--;
			}
		}
		return age;
	}

	/**
	 * 获取星座
	 * 
	 * @param month
	 *            月份
	 * @param day
	 *            日期
	 * @return 星座
	 */
	public static String getAstro(int month, int day) {
		String[] constellationArr = {
				"水瓶座",
				"双鱼座",
				"白羊座",
				"金牛座",
				"双子座",
				"巨蟹座",
				"狮子座",
				"处女座",
				"天秤座",
				"天蝎座",
				"射手座",
				"魔羯座" };

		int[] constellationEdgeDay = { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
		month = month - 1;
		if (day < constellationEdgeDay[month]) {
			month = month - 1;
		}
		if (month >= 0) {
			return constellationArr[month];
		}
		// default to return 魔羯
		return constellationArr[11];
	}

	/**
	 * 
	 * @param time
	 *            秒为单位
	 * @return
	 */
	public static String getCommentTime(long commentTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		Date date = new Date(commentTime * 1000);
		long diff = System.currentTimeMillis() - date.getTime();
		if (diff <= 3 * 60 * 1000) {
			return "刚刚";
		} else if (diff <= 60 * 60 * 1000) {
			return diff / 1000 / 60 + "分钟前";
		} else if (diff <= 24 * 60 * 60 * 1000) {
			return diff / 1000 / 60 / 60 + "小时前";
		} else if (diff <= 15 * 24 * 60 * 60 * 1000) {
			return diff / 1000 / 60 / 60 / 24 + "天前";
		}
		return format.format(date);
	}

	public static int getDayNum(int year, int month) {
		String[] big_months = { "1", "3", "5", "7", "8", "10", "12" };
		String[] little_months = { "4", "6", "9", "11" };
		final List<String> list_big = Arrays.asList(big_months);
		final List<String> list_little = Arrays.asList(little_months);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			return 31;
		} else if (list_little.contains(String.valueOf(month + 1))) {
			return 30;
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				return 29;
			else {
				return 28;
			}
		}
	}

	/**
	 * 
	 * @param diff
	 *            秒为单位
	 * @return
	 */
	public static String getLeftServiceTime(long diff) {
		if (diff <= 0) {
			return "00:00:00";
		}
		long days = diff / (60 * 60 * 24);
		long hour = diff % (60 * 60 * 24) / (60 * 60);
		long mins = diff % (60 * 60 * 24) % (60 * 60) / 60;
		long seconds = diff % (60 * 60 * 24) % (60 * 60) % 60 % 60;
		String result = "";
		if (days > 0) {
			result += days + "天";
		}
		if (hour < 10) {
			result += "0" + hour + "时";
		} else {
			result += hour + "时";
		}

		if (mins < 10) {
			result += "0" + mins + "分";
		} else {
			result += mins + "分";
		}
		if (seconds < 10) {
			result += "0" + seconds + "秒";
		} else {
			result += seconds + "秒";
		}
		return result;
	}
}
