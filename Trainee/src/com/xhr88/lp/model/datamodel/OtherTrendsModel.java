package com.xhr88.lp.model.datamodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.xhr.framework.orm.BaseModel;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.util.DateUtil;

public class OtherTrendsModel extends BaseModel {

	private static final long serialVersionUID = 5037177274512550126L;
	private String uid;
	private String maxid;
	private String nickname;
	private int level;
	/**
	 * 性别 1=男，2=女
	 */
	private int sex;
	/**
	 * 生日
	 */
	private String birth;
	private int isvideo; // 0为未通过验证，1为通过
	private String headimg;
	private String background;
	private List<OtherSubTrendsModel> list;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMaxid() {
		return maxid;
	}

	public void setMaxid(String maxid) {
		this.maxid = maxid;
	}

	public String getBackground() {
		return null == background ? "" : background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public List<OtherSubTrendsModel> getList() {
		return list;
	}

	public void setList(List<OtherSubTrendsModel> list) {
		this.list = list;
	}

	public String getNickname() {
		return nickname;
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
		return null == headimg ? "" : headimg;
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
