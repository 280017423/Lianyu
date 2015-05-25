package com.xhr88.lp.util;

import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.util.Log;

/**
 * @Version 1.0
 * @Author zou.sq
 * @Creation 2015-4-8 下午5:26:50
 * @Modification 2015-4-8 下午5:26:50
 */
@MessageTag(value = "RC:LP")
public class LpMessage extends RongIMClient.MessageContent {

	private String content;
	private String extra;

	public LpMessage(byte[] data) {
		String jsonStr = null;

		try {
			jsonStr = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e1) {

		}

		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			setContent(jsonObj.getString("content"));
			setExtra(jsonObj.getString("extra"));

		} catch (JSONException e) {
			Log.e("JSONException", e.getMessage());
		}

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}

	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("content", content);
			jsonObj.put("extra", extra);

		} catch (JSONException e) {
			Log.e("JSONException", e.getMessage());
		}
		try {
			return jsonObj.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

}
