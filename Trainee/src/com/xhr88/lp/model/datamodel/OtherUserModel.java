package com.xhr88.lp.model.datamodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.xhr.framework.orm.BaseModel;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.util.DateUtil;

public class OtherUserModel extends BaseModel {

	private static final long serialVersionUID = -3948395425572620699L;

	private String touid;
	private String nickname;
	private int level;
	private int isvideo;
	private int sex;
	private String birth;
	private String trendssum;
	private String content;
	private String resource;
	private int createtime;
	private int type;
	private int chatstatus;
	private String storeinfo;
	private String background;
	private int relation; // 与我的关系。1=关注, 0=未关注
	private String photo;
	private String bigphoto;
	private String usertype; // 用户所属分类，用，分隔 如 1,2,3

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
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

	public String getTrendssum() {
		return trendssum;
	}

	public void setTrendssum(String trendssum) {
		this.trendssum = trendssum;
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

	public int getCreatetime() {
		return createtime;
	}

	public void setCreatetime(int createtime) {
		this.createtime = createtime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getChatstatus() {
		return chatstatus;
	}

	public void setChatstatus(int chatstatus) {
		this.chatstatus = chatstatus;
	}

	public String getStoreinfo() {
		return storeinfo;
	}

	public void setStoreinfo(String storeinfo) {
		this.storeinfo = storeinfo;
	}

	public String getTouid() {
		return touid;
	}

	public void setTouid(String touid) {
		this.touid = touid;
	}

	public int getRelation() {
		return relation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getBigphoto() {
		return bigphoto;
	}

	public void setBigphoto(String bigphoto) {
		this.bigphoto = bigphoto;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public List<PictureModel> getPictureModels() {
		if (StringUtil.isNullOrEmpty(photo) || StringUtil.isNullOrEmpty(bigphoto)) {
			return new ArrayList<PictureModel>();
		}
		List<PictureModel> models = new ArrayList<PictureModel>();
		String[] photos = photo.split(",");
		String[] bigphotos = bigphoto.split(",");
		for (int i = 0; i < bigphotos.length; i++) {
			PictureModel model = new PictureModel();
			model.setPhoto(photos[i]);
			model.setBigphoto(bigphotos[i]);
			models.add(model);
		}
		return models;
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
