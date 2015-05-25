package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;
import com.xhr.framework.util.DateUtil;
import com.xhr.framework.util.StringUtil;

public class HistoryPayModel extends BaseModel {

	private static final long serialVersionUID = -2439384230057381798L;
	private String coin;
	private String info;
	private String paytime;

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public String getInfo() {
		return null == info ? "" : info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getPaytime() {
		return paytime;
	}

	public void setPaytime(String paytime) {
		this.paytime = paytime;
	}

	public String getDisplayPaydate() {
		if (StringUtil.isNullOrEmpty(paytime)) {
			return "";
		}
		return DateUtil.formatTime(paytime, DateUtil.DEFAULT_DATE_FORMAT);
	}

	public String getDisplayPaytime() {
		if (StringUtil.isNullOrEmpty(paytime)) {
			return "";
		}
		return DateUtil.formatTime(paytime, "HH:mm:ss");
	}

}
