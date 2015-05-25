package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class TrendsDetailsModel extends BaseModel{

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9019840206027910990L;
	private int uid;
	private String nickname;
	private int level;
	//是否通过视频验证0=未通过，1=通过
	private int isvideo;
	private String headimg;
	//性别 1=男，2=女
	private int sex;
	//是否关注。0=未关注，1=已关注
	private int isfollow;
	private String content;
	private String resource;
	private int createtime;
	private int commentnum;
	private int goodnum;
	private int isgood;
	//动态类型动态类型，1=文本，2=图片，3=语言，4=视频
	private int trendstype;
	private int chatstatus;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
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
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getIsfollow() {
		return isfollow;
	}
	public void setIsfollow(int isfollow) {
		this.isfollow = isfollow;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public int getCreatetime() {
		return createtime;
	}
	public void setCreatetime(int createtime) {
		this.createtime = createtime;
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
	public int getIsgood() {
		return isgood;
	}
	public void setIsgood(int isgood) {
		this.isgood = isgood;
	}
	public int getTrendstype() {
		return trendstype;
	}
	public void setTrendstype(int trendstype) {
		this.trendstype = trendstype;
	}
	public int getChatstatus() {
		return chatstatus;
	}
	public void setChatstatus(int chatstatus) {
		this.chatstatus = chatstatus;
	}
	
	
	
	
	
	
}
