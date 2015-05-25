package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class TaskModel extends BaseModel {

	private static final long serialVersionUID = 6043054847706510248L;
	private int taskid; // 1是签到，2是分享
	private String title; // 任务标题
	private int coin; // 任务完成时的金币
	private int getstatus; // 领取状态:0=未分享，1=已领取, 2=未完成（签到或分享）

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public String getTitle() {
		return title;
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

	public int getGetstatus() {
		return getstatus;
	}

	public void setGetstatus(int getstatus) {
		this.getstatus = getstatus;
	}

}
