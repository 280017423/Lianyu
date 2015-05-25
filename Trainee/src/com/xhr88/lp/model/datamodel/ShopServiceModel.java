package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class ShopServiceModel extends BaseModel {

	private static final long serialVersionUID = -7683462546127091196L;
	private String sid; // 服务ID
	private String title; // 服务标题
	private int coin; // 消耗金币
	private String unit; // 单位，小时，天，月
	private String explain; // 服务说明
	private int open; // 是否打开此项服务 0=不打开，1=打开
	private String logo; // 服务图标
	private String closelogo; // 关闭服务的图标
	private String info; // 关闭服务的图标
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
		return null == explain ? "" : explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public int getOpen() {
		return open;
	}

	public void setOpen(int open) {
		this.open = open;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getCloselogo() {
		return closelogo;
	}

	public void setCloselogo(String closelogo) {
		this.closelogo = closelogo;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

}
