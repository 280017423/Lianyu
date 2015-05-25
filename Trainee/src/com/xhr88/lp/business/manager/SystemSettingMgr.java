package com.xhr88.lp.business.manager;

import java.io.File;

import android.content.Context;

import com.xhr.framework.util.AppUtil;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.FileUtil;

/**
 * @author xu.xb
 * @version 2013-8-13 下午5:33:05 xu.xb 创建类<br>
 */
public class SystemSettingMgr {
	private static final String TAG = "SystemSettingMgr";

	/**
	 * @param context
	 *            上下文对象
	 */
	public static void deleteFiles(Context context) {
		if (context != null) {
			FileUtil.deleteDirRecursive(getNoSdcardFile(context));
			FileUtil.deleteDirRecursive(getSdcardImageCacheFile(context));
		}
	}

	/**
	 * @param context
	 *            上下文对象
	 * @return size
	 */
	public static String getCacheFileSize(Context context) {
		if (context != null) {
			String fileSizeString = "";
			long filesize = FileUtil.getFileSize(getSdcardImageCacheFile(context))
					+ FileUtil.getFileSize(getNoSdcardFile(context));
			fileSizeString = FileUtil.formatFileSize(filesize);
			return fileSizeString;
		}
		return "";
	}

	/**
	 * 读取SD卡中图片缓存的文件夹
	 * 
	 * @return File
	 * @throws
	 */
	private static File getSdcardImageCacheFile(Context context) {
		File mCacheDir = null;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			String imageDir = AppUtil.getMetaDataByKey(context, "image_dir");
			mCacheDir = new File(android.os.Environment.getExternalStorageDirectory(), imageDir);
			EvtLog.d(TAG, "image mcacheDir: " + mCacheDir.getAbsolutePath());
			long size = FileUtil.getFileSize(mCacheDir);
			String filesize = FileUtil.formatFileSize(size);
			EvtLog.d(TAG, "sdcard image cache file size: " + mCacheDir.getAbsolutePath());
			EvtLog.d(TAG, "sdcard image cache file size: " + size);
			EvtLog.d(TAG, "转换后sdcard image cache file size: " + filesize);
		}
		return mCacheDir;
	}

	/**
	 * 没有SD卡时，图片会缓存在系统默认的文件夹中
	 * 
	 * @param context
	 *            上下文
	 * @return File
	 * @throws
	 */
	private static File getNoSdcardFile(Context context) {
		File mCacheDir = context.getCacheDir();
		EvtLog.d(TAG, "no sdcard mcacheDir: " + mCacheDir.getAbsolutePath());
		long size = FileUtil.getFileSize(mCacheDir);
		EvtLog.d(TAG, "no sdcard cache file size: " + size);
		String filesize = FileUtil.formatFileSize(size);
		EvtLog.d(TAG, "转换后no sdcard cache file size: " + filesize);
		return mCacheDir;
	}
}
