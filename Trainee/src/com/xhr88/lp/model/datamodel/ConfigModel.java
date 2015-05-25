package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class ConfigModel extends BaseModel {

	private static final long serialVersionUID = -5759641984309748290L;
	private int isnewkeyword; // 是否需要强制更新过滤关键字，0=不需要，1=需要，调用过滤关键字接口
	private int systemtime; // 当前服务器系统时间
	private int verid; // 启动图+引导图服务器端版本号，为正整数
	private String guideimg; // 预留字段
	private int isupdate; // 是否有新版本 0=无，1=有新版本
	private int mustupdate; // 是否强制更新新版本 0=不强制，1=强制
	private String updatever; // 新版本号

	public int getIsnewkeyword() {
		return isnewkeyword;
	}

	public void setIsnewkeyword(int isnewkeyword) {
		this.isnewkeyword = isnewkeyword;
	}

	public int getSystemtime() {
		return systemtime;
	}

	public void setSystemtime(int systemtime) {
		this.systemtime = systemtime;
	}

	public int getVerid() {
		return verid;
	}

	public void setVerid(int verid) {
		this.verid = verid;
	}

	public String getGuideimg() {
		return guideimg;
	}

	public void setGuideimg(String guideimg) {
		this.guideimg = guideimg;
	}

	public int getIsupdate() {
		return isupdate;
	}

	public void setIsupdate(int isupdate) {
		this.isupdate = isupdate;
	}

	public String getUpdatever() {
		return null == updatever ? "" : updatever;
	}

	public void setUpdatever(String updatever) {
		this.updatever = updatever;
	}

	public int getMustupdate() {
		return mustupdate;
	}

	public void setMustupdate(int mustupdate) {
		this.mustupdate = mustupdate;
	}

}
