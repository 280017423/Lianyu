package com.xhr88.lp.util;

import android.util.Base64;

import com.xhr.framework.util.EvtLog;

//import com.pdw.framework.util.EvtLog;

/**
 * YWBase64.java Description:
 * 
 * @author huang.b
 * @data 2014-9-15
 * 
 */

public class YWBase64 {
	private static final String TAG = "YWBase64";

	public static String decodeToString(String str) {
		if (str == null) {
			return "";
		}
		String decodeString = "";
		try {
			decodeString = new String(Base64.decode(str, Base64.NO_WRAP));
		} catch (Exception e) {
			EvtLog.d(TAG, "非base64编码");
		}
		return decodeString.trim();
	}

	
	/**
	 * 编码
	 * 
	 * @param str
	 * @return
	 */
	public static String encedoToString(String str) {
		if (str == null) {
			return "";
		}
		String encodeContent = "";
		try {
			encodeContent = Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
		} catch (Exception e) {
			EvtLog.d(TAG, "编码出现异常");
		}
		return encodeContent.trim();
	}
}
