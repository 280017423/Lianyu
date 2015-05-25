package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class ServiceModel extends BaseModel {

	private static final long serialVersionUID = -3211207167859465993L;
	private int type; // 服务关系，1=无购买服务，2=购买服务正常中，3=购买服务已结束
	private String sid; // 服务id
	private String bid; // 服务购买表id
	private String uid; // 用户方 买方
	private String buyuid; // 服务方 卖方
	private String title; // 服务标题
	private int endtime; // 结束剩余时间
	private int grade; // 评分

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getEndtime() {
		return endtime;
	}

	public void setEndtime(int endtime) {
		this.endtime = endtime;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getBuyuid() {
		return buyuid;
	}

	public void setBuyuid(String buyuid) {
		this.buyuid = buyuid;
	}

}
