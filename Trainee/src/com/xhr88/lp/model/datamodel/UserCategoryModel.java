package com.xhr88.lp.model.datamodel;

import com.xhr.framework.orm.BaseModel;
import com.xhr.framework.orm.annotation.Transient;

public class UserCategoryModel extends BaseModel {

	private static final long serialVersionUID = 8703982952823661821L;

	/*
	 * 分类ID
	 */
	private int catid;

	/*
	 * 1：男，2：女
	 */
	private int type;

	/*
	 * 分类名称
	 */
	private String categoryname;

	@Transient
	private boolean isChecked;

	public int getCatid() {
		return catid;
	}

	public void setCatid(int catid) {
		this.catid = catid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCategoryname() {
		return null == categoryname ? "" : categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public boolean getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
