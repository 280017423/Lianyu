package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;

public class PictureModel extends BaseModel {

	private static final long serialVersionUID = -4288643064829891090L;
	private String photoid;
	private String photo;
	private String bigphoto;

	public String getPhotoid() {
		return photoid;
	}

	public void setPhotoid(String photoid) {
		this.photoid = photoid;
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

}
