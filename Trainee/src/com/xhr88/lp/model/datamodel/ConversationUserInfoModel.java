package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

// import com.pdw.yw.util.YWBase64;

/**
 * 用户信息类,用于操作Preference；
 * 
 * @author
 * @version
 */
public class ConversationUserInfoModel extends BaseModel {

	private static final long serialVersionUID = 136835390075044490L;
	private String uid;
	private String nickname;
	private Integer level;
	private Integer isvideo;
	private String headimg;
	private Integer sex;
	private String birth;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getIsvideo() {
		return isvideo;
	}

	public void setIsvideo(Integer isvideo) {
		this.isvideo = isvideo;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

}
