package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class PayModel extends BaseModel {

	private static final long serialVersionUID = -1899536688611094131L;
	private float money; // 充值金额
	private int coin; // 充值后兑换的恋爱币
	private int give; // 充值赠送的恋爱币
	private String info; // 说明

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public String getInfo() {
		return null == info ? "" : info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getGive() {
		return give;
	}

	public void setGive(int give) {
		this.give = give;
	}

}
