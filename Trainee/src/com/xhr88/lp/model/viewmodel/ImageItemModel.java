package com.xhr88.lp.model.viewmodel;

import java.io.Serializable;

/**
 * 一个图片对象
 */
public class ImageItemModel implements Serializable {
	private static final long serialVersionUID = 9036856688204096830L;
	public String imageId;
	public String uuid;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected;

}
