package com.xhr88.lp.model.viewmodel;

import java.io.Serializable;
import java.util.List;

public class TrainPersonEditViewModel implements Serializable {

	private static final long serialVersionUID = 1154190726016913914L;
	private int state;
	private String msg;
	private int level;
	private String nickname;
	private String birth;
	private int isvideo;
	private int sex;
	private String background;
	private List<String> list;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public int getIsvideo() {
		return isvideo;
	}

	public void setIsvideo(int isvideo) {
		this.isvideo = isvideo;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
