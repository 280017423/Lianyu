package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class BuyServiceModel extends BaseModel {

	private static final long serialVersionUID = -2439384230057381798L;
	private String sid; // 服务id
	private String title; // 服务标题
	private int coin; // 消耗金币
	private String unit; // 单位，分钟 小时，天，月
	private String explain; // 服务说明
	private int length; // 时长
	private String info; // 服务详细
	private String logo; // 服务logo
	private boolean isShow; // 是否显示

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getTitle() {
		return null == title ? "" : title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public String getInfo() {
		return null == info ? "" : info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
