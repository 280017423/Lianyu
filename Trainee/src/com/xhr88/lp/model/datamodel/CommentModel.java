package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class CommentModel extends BaseModel {

	private static final long serialVersionUID = 4371722797297951913L;
	private String tid; // 评论id
	private String cid; // 评论id
	private String uid; // 用户id
	private String headimg;
	private String nickname; // 昵称
	private String content; // 评论内容
	private int sex; // 性别1=男；2=女
	private int type; // 1=直接回复动态，2=评论动态中针对某人的回复
	private String comuid; // 针对某人回复时的uid
	private String comnickname; // 针对某人回复时的昵称
	private long createtime; // 发布时间
	

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getComuid() {
		return comuid;
	}

	public void setComuid(String comuid) {
		this.comuid = comuid;
	}

	public String getComnickname() {
		return comnickname;
	}

	public void setComnickname(String comnickname) {
		this.comnickname = comnickname;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

}
