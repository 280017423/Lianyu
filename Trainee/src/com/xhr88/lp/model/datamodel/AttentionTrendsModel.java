package com.xhr88.lp.model.datamodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.xhr.framework.orm.BaseModel;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.util.DateUtil;

public class AttentionTrendsModel extends BaseModel {

	private static final long serialVersionUID = 5037177274512550126L;
	private String uid;
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
	private String tid;
	private String content;
	private String resource;
	private String bigresource;
	private int commentnum;
	private int goodnum;
	private int isgood;
	private int lng;
	private int lat;
	private int trendstype; // 动态类型动态类型，1=文本，2=图片，3=语言，4=视频
	private int createtime;
	private List<CommentModel> commentlist;

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

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getResource() {
		return null == resource ? "" : resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public int getCommentnum() {
		return commentnum;
	}

	public void setCommentnum(int commentnum) {
		this.commentnum = commentnum;
	}

	public int getGoodnum() {
		return goodnum;
	}

	public void setGoodnum(int goodnum) {
		this.goodnum = goodnum;
	}

	public int getLng() {
		return lng;
	}

	public void setLng(int lng) {
		this.lng = lng;
	}

	public int getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public int getTrendstype() {
		return trendstype;
	}

	public void setTrendstype(int trendstype) {
		this.trendstype = trendstype;
	}

	public int getCreatetime() {
		return createtime;
	}

	public void setCreatetime(int createtime) {
		this.createtime = createtime;
	}

	public List<CommentModel> getCommentlist() {
		return commentlist;
	}

	public void setCommentlist(List<CommentModel> commentlist) {
		this.commentlist = commentlist;
	}

	public boolean isVideo() {
		return 4 == trendstype;
	}

	public int getIsgood() {
		return isgood;
	}

	public void setIsgood(int isgood) {
		this.isgood = isgood;
	}

	public String getBigresource() {
		return bigresource;
	}

	public void setBigresource(String bigresource) {
		this.bigresource = bigresource;
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
