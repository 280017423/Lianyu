package com.xhr88.lp.model.datamodel;

import java.util.List;

import com.xhr.framework.orm.BaseModel;

public class OtherSubTrendsModel extends BaseModel {

	private static final long serialVersionUID = 5037177274512550126L;
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
		return resource;
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

}
