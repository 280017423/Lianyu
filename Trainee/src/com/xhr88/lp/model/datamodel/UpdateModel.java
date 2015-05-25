package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class UpdateModel extends BaseModel {

	private static final long serialVersionUID = 1435189185653303412L;
	private int isupdate; // 1=无新版本，2=有新版本
	private String infourl; // 新版本介绍网页地址
	private String updateurl; // 新版本更新的url地址
	private String updatever; // 新版本号
	private String verinfo; // 新版本更新点说明

	public int getIsupdate() {
		return isupdate;
	}

	public void setIsupdate(int isupdate) {
		this.isupdate = isupdate;
	}

	public String getInfourl() {
		return null == infourl ? "" : infourl;
	}

	public void setInfourl(String infourl) {
		this.infourl = infourl;
	}

	public String getUpdateurl() {
		return updateurl;
	}

	public void setUpdateurl(String updateurl) {
		this.updateurl = updateurl;
	}

	public String getUpdatever() {
		return updatever;
	}

	public void setUpdatever(String updatever) {
		this.updatever = updatever;
	}

	public String getVerinfo() {
		return verinfo;
	}

	public void setVerinfo(String verinfo) {
		this.verinfo = verinfo;
	}

}
