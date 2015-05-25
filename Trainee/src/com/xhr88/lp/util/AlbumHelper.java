package com.xhr88.lp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;

import com.xhr.framework.util.EvtLog;
import com.xhr88.lp.model.viewmodel.ImageAlbumModel;
import com.xhr88.lp.model.viewmodel.ImageItemModel;

/**
 * 
 * 相册工具类
 * 
 * @author Administrator
 * 
 */
public class AlbumHelper {
	private static final String TAG = "AlbumHelper";
	private static AlbumHelper INSTANCE;
	private boolean mHasBuildImagesBucketList;

	private Context mContext;
	private ContentResolver mResolver;
	private HashMap<String, String> mThumbnailMap = new HashMap<String, String>();
	private List<HashMap<String, String>> mAlbumList = new ArrayList<HashMap<String, String>>();
	private HashMap<String, ImageAlbumModel> mBucketMap = new HashMap<String, ImageAlbumModel>();

	private AlbumHelper() {
	}

	/**
	 * @Description 单例模式
	 * @return 当前实例
	 */
	public static AlbumHelper getHelper() {
		if (INSTANCE == null) {
			INSTANCE = new AlbumHelper();
		}
		return INSTANCE;
	}

	/**
	 * 
	 * @Description 初始化
	 * @param context
	 *            上下文对象
	 * 
	 */
	public void init(Context context) {
		if (this.mContext == null) {
			this.mContext = context;
			mResolver = context.getContentResolver();
		}
	}

	public List<ImageAlbumModel> getImagesBucketList(boolean refresh) {
		if (refresh || (!refresh && !mHasBuildImagesBucketList)) {
			mThumbnailMap.clear();
			mAlbumList.clear();
			mBucketMap.clear();
			buildImagesBucketList();
		}
		List<ImageAlbumModel> tmpList = new ArrayList<ImageAlbumModel>();
		Iterator<Entry<String, ImageAlbumModel>> itr = mBucketMap.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, ImageAlbumModel> entry = (Map.Entry<String, ImageAlbumModel>) itr.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}

	private void buildImagesBucketList() {
		Cursor cur = null;
		try {
			// 构造缩略图索引
			getThumbnail();
			// 构造相册索引
			String columns[] = new String[] {
					Media._ID,
					Media.BUCKET_ID,
					Media.PICASA_ID,
					Media.DATA,
					Media.DISPLAY_NAME,
					Media.TITLE,
					Media.SIZE,
					Media.BUCKET_DISPLAY_NAME };
			// 得到一个游标
			cur = mResolver.query(Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
			if (cur.moveToFirst()) {
				// 获取指定列的索引
				int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
				int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
				// int photoNameIndex =
				// cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
				// int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
				// int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
				int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
				int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
				// int picasaIdIndex =
				// cur.getColumnIndexOrThrow(Media.PICASA_ID);
				do {
					String _id = cur.getString(photoIDIndex);
					// String name = cur.getString(photoNameIndex);
					String path = cur.getString(photoPathIndex);
					// String title = cur.getString(photoTitleIndex);
					// String size = cur.getString(photoSizeIndex);
					String bucketName = cur.getString(bucketDisplayNameIndex);
					String bucketId = cur.getString(bucketIdIndex);
					// String picasaId = cur.getString(picasaIdIndex);

					ImageAlbumModel bucket = mBucketMap.get(bucketId);
					if (bucket == null) {
						bucket = new ImageAlbumModel();
						mBucketMap.put(bucketId, bucket);
						bucket.imageList = new ArrayList<ImageItemModel>();
						bucket.bucketName = bucketName;
					}
					bucket.count++;
					ImageItemModel imageItem = new ImageItemModel();
					imageItem.imageId = _id;
					// 生成UUID，上传七牛服务器
					imageItem.uuid = UUID.randomUUID() + "";
					imageItem.imagePath = path;
					imageItem.thumbnailPath = mThumbnailMap.get(_id);
					bucket.imageList.add(imageItem);

				} while (cur.moveToNext());
			}
			mHasBuildImagesBucketList = true;
		} catch (Exception e) {
			EvtLog.e(TAG, e.getMessage());
		} finally {
			if (cur != null && !cur.isClosed()) {
				cur.close();
			}
		}
	}

	private void getThumbnail() {
		Cursor cursor = null;
		try {
			String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA };
			cursor = mResolver.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
			getThumbnailColumnData(cursor);
		} catch (Exception e) {
			EvtLog.e(TAG, e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	private void getThumbnailColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			// int _id;
			int image_id;
			String image_path;
			// int _idColumn = cur.getColumnIndex(Thumbnails._ID);
			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

			do {
				// _id = cur.getInt(_idColumn);
				image_id = cur.getInt(image_idColumn);
				image_path = cur.getString(dataColumn);
				mThumbnailMap.put("" + image_id, image_path);
			} while (cur.moveToNext());
		}
	}
}
