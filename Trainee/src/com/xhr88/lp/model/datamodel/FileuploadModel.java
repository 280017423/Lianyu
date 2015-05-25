package com.xhr88.lp.model.datamodel;

import java.io.Serializable;

public class FileuploadModel implements Serializable {

	private static final long serialVersionUID = 5305560649758578414L;
	public static final String SHAREPREFERENCES_NAME = "com.xhr88.lp.fileupload";
	private String filetoken;
	private int expire;

	public String getFiletoken() {
		return filetoken;
	}

	public void setFiletoken(String filetoken) {
		this.filetoken = filetoken;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

}
