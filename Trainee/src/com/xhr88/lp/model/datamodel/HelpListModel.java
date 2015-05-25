package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class HelpListModel extends BaseModel {

	private static final long serialVersionUID = 7945016422576762317L;

	private String hid; // 帮助id
	private String title; // 帮助标题
	private String content; // 帮助内容

	public String getHid() {
		return hid;
	}

	public void setHid(String hid) {
		this.hid = hid;
	}

	public String getTitle() {
		return null == title ? "" : title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
