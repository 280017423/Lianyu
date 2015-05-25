package com.xhr88.lp.model.datamodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.xhr.framework.orm.BaseModel;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.util.DateUtil;

public class TrendsInfoModel extends BaseModel {

	private static final long serialVersionUID = -5006390971610108289L;

	private String uid; // 用户ID
	private String nickname; // 昵称
	private int level; // 等级
	private int isvideo; // 是否通过视频验证0=未通过，1=通过
	private String headimg; // 用户头像小缩略图
	private String birth; // 生日
	private int sex; // 性别 1=男，2=女
	private int isfollow; // 是否关注。0=未关注，1=已关注
	private String content; // 动态内容
	private String resource; // 资源，图片地址（中等缩略图450*450），多个资源以 ， 分隔
	private String bigresource; // 大图资源，图片地址（中等缩略图450*450），多个资源以 ， 分隔
	private long createtime; // 发表时间
	private int commentnum; // 评论数
	private int goodnum; // 喜欢数
	private int isgood; // 是否赞过
	private int trendstype; // 动态类型动态类型，1=文本，2=图片，3=语言，4=视频
	private int chatstatus; // 聊天状态
							// 1=未签约不显示,2=购买了服务显示聊天中，3=签约用户正常接单中显示约可点击进入购买，4=签约用户暂不提供服务显示，但不可点击；
	private String shareurl; // 分享路径

	// 1=未签约不显示约,2=购买了服务显示聊天中，3=签约用户正常接单中显示约可点击进入购买，4=签约用户暂不提供服务显示约，但不可点击；
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

	public int getIsfollow() {
		return isfollow;
	}

	public void setIsfollow(int isfollow) {
		this.isfollow = isfollow;
	}

	public String getContent() {
		return null == content ? "" : content;
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

	public String getBigresource() {
		return bigresource;
	}

	public void setBigresource(String bigresource) {
		this.bigresource = bigresource;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
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

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getShareurl() {
		return null == shareurl ? "" : shareurl;
	}

	public void setShareurl(String shareurl) {
		this.shareurl = shareurl;
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
