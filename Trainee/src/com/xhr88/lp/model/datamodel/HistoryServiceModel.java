package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class HistoryServiceModel extends BaseModel {

	private static final long serialVersionUID = -2439384230057381798L;
	private String uid; // 用户id
	private String bid; // 购买记录id
	private String nickname; // 昵称
	private int sex; // 性别1=男，2=女
	private String coin; // 花费币数，或得到币数
	private int starttime; // 服务购买时间
	private int endtime; // 服务结束时间截
	private int estimate; // 评价星数
	private String headimg; // 头像地址
	private int status; // 状态：1=未到账，2=已到帐

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getNickname() {
		return null == nickname ? "" : nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getCoin() {
		return null == coin ? "" : coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public int getStarttime() {
		return starttime;
	}

	public void setStarttime(int starttime) {
		this.starttime = starttime;
	}

	public int getEndtime() {
		return endtime;
	}

	public void setEndtime(int endtime) {
		this.endtime = endtime;
	}

	public int getEstimate() {
		return estimate;
	}

	public void setEstimate(int estimate) {
		this.estimate = estimate;
	}

	public String getHeadimg() {
		return null == headimg ? "" : headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
