package com.xhr88.lp.model.datamodel;

import java.util.List;

import com.xhr.framework.orm.BaseModel;

public class ShopInfoModel extends BaseModel {

	private static final long serialVersionUID = 7945016422576762317L;

	private String description; // 小店描述
	private int storestatus; // 是否提供服务状态1=审核中，2=审核不通过，3=通过不提供服务，4=提供服务
	private String maxserve; // 最大服务人数的选项 符号 ,分隔
	private int servenum; // 最大服务人数
	private String usertype; // 用户所属分类，用，分隔 如 1,2,3
	private List<ShopServiceModel> list;// 服务列表

	public String getDescription() {
		return null == description ? "" : description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStorestatus() {
		return storestatus;
	}

	public void setStorestatus(int storestatus) {
		this.storestatus = storestatus;
	}

	public String getMaxserve() {
		return null == maxserve ? "" : maxserve;
	}

	public void setMaxserve(String maxserve) {
		this.maxserve = maxserve;
	}

	public int getServenum() {
		return servenum;
	}

	public void setServenum(int servenum) {
		this.servenum = servenum;
	}

	public List<ShopServiceModel> getList() {
		return list;
	}

	public void setList(List<ShopServiceModel> list) {
		this.list = list;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String[] getDisplayPeopleNum() {
		String[] nums = getPeopleNum();
		String[] newNums = new String[nums.length];
		for (int i = 0; i < nums.length; i++) {
			newNums[i] = nums[i] + "人";
		}
		return newNums;
	}

	public String[] getPeopleNum() {
		return getMaxserve().split(",");
	}

	public int getPeopleIndex() {
		String[] nums = getPeopleNum();
		for (int i = 0; i < nums.length; i++) {
			if (servenum == Integer.parseInt(nums[i])) {
				return i;
			}
		}
		return 0;
	}
}
