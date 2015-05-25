package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class RecommendListModel extends BaseModel {

	private static final long serialVersionUID = 6603467035332631212L;
	/**
	 * 用户id
	 */
	private String uid;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 用户级别
	 */
	private int level;
	/**
	 * 动态id
	 */
	private String tid;
	/**
	 * 动态内容
	 */
	private String content;
	/**
	 * 首张图片
	 */
	private String resource;
	/**
	 * 动态类型动态类型，1=文本，2=图片，3=语言，4=视频
	 */
	private int trendstype;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getNickname() {
		return null == nickname ? "" : nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getContent() {
		return null == content ? "" : content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getResource() {
		return null == resource ? "" : resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public int getTrendstype() {
		return trendstype;
	}

	public boolean isVideo() {
		return 4 == trendstype;
	}

	public void setTrendstype(int trendstype) {
		this.trendstype = trendstype;
	}

}
