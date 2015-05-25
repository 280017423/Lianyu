package com.xhr88.lp.model.datamodel;

import java.io.Serializable;
import java.util.List;

// import com.pdw.yw.util.YWBase64;

/**
 * 用户信息类,用于操作Preference；
 * 
 * @author
 * @version
 */
public class UserInfoModel implements Serializable {

	private static final long serialVersionUID = -7210781153327092952L;
	private String uid;
	private String token;
	private String nickname;
	private Integer sex;
	private String birth;
	private Integer age;
	private Integer level;
	private Integer isvideo;
	private String constellation;
	private String headimg;
	private Integer coin;
	private String imtoken;
	private String background;
	private Integer trendsnum;
	private Integer follownum;
	private Integer fansnum;
	private Integer isnewfans;
	private Integer newtrends;
	private List<PictureModel> list;

	public String getNickname() {
		return null == nickname ? "" : nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getBirth() {
		return null == birth ? "" : birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getHeadimg() {
		return null == headimg ? "" : headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public String getUid() {
		return null == uid ? "" : uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
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

	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}

	public Integer getCoin() {
		return coin;
	}

	public void setCoin(Integer coin) {
		this.coin = coin;
	}

	public String getImtoken() {
		return imtoken;
	}

	public void setImtoken(String imtoken) {
		this.imtoken = imtoken;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public Integer getTrendsnum() {
		return trendsnum;
	}

	public void setTrendsnum(Integer trendsnum) {
		this.trendsnum = trendsnum;
	}

	public Integer getFollownum() {
		return follownum;
	}

	public void setFollownum(Integer follownum) {
		this.follownum = follownum;
	}

	public Integer getFansnum() {
		return fansnum;
	}

	public void setFansnum(Integer fansnum) {
		this.fansnum = fansnum;
	}

	public Integer getIsnewfans() {
		return isnewfans;
	}

	public void setIsnewfans(Integer isnewfans) {
		this.isnewfans = isnewfans;
	}

	public Integer getNewtrends() {
		return newtrends;
	}

	public void setNewtrends(Integer newtrends) {
		this.newtrends = newtrends;
	}

	public List<PictureModel> getList() {
		return list;
	}

	public void setList(List<PictureModel> list) {
		this.list = list;
	}
}
