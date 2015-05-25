package com.xhr88.lp.model.datamodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.xhr.framework.orm.BaseModel;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.util.DateUtil;

public class AttentionModel extends BaseModel {

	private static final long serialVersionUID = -3588008832385555626L;
	/**
	 * 用户id
	 */
	private String uid;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 用户级别
	 */
	private int level;
	/**
	 * 是否通过视频验证0=未通过，1=通过
	 */
	private int isvideo;
	/**
	 * 用户头像小缩略图
	 */
	private String headimg;
	/**
	 * 性别 1=男，2=女
	 */
	private int sex;
	/**
	 * 生日
	 */
	private String birth;
	/**
	 * 与我的关系。1=关注，2=互相关注（互粉），0=未关注
	 */
	private int relation;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getNickname() {
		return null == nickname ? "" : nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getIsvideo() {
		return isvideo;
	}

	public void setIsvideo(int isvideo) {
		this.isvideo = isvideo;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public int getRelation() {
		return relation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	public String getAge() throws Exception {
		if (StringUtil.isNullOrEmpty(birth)) {
			return "";
		}
		SimpleDateFormat fromFormatter = new SimpleDateFormat(
				com.xhr.framework.util.DateUtil.DEFAULT_DATE_FORMAT, Locale.getDefault());
		Date birthDay = fromFormatter.parse(birth);
		return DateUtil.getAgeByDate(birthDay) + "";
	}

	public CharSequence getConstellatory() {
		if (StringUtil.isNullOrEmpty(birth)) {
			return "";
		}
		String[] ymd = birth.split("-");
		return DateUtil.getAstro(Integer.parseInt(ymd[1]), Integer.parseInt(ymd[2]));
	}

}
