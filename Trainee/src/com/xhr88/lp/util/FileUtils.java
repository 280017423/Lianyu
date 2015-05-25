package com.xhr88.lp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import com.xhr88.lp.app.TraineeApplication;
import com.xhr88.lp.update.NotificationReceiver;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

/**
 * @fileName FileUtils.java
 * @package
 * @description 文件工具类
 * @author
 * @email
 * @version 1.0
 */
public class FileUtils {
	/**
	 * 判断SD是否可以
	 * 
	 * @return
	 */
	public static boolean isSdcardExist() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 创建根目录
	 * 
	 * @param path
	 *            目录路径
	 */
	public static void createDirFile(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param path
	 *            文件路径
	 * @return 创建的文件
	 */
	public static File createNewFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return file;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folderPath
	 *            文件夹的路径
	 */
	public static void delFolder(String folderPath) {
		delAllFile(folderPath);
		String filePath = folderPath;
		filePath = filePath.toString();
		java.io.File myFilePath = new java.io.File(filePath);
		myFilePath.delete();
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 *            文件的路径
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
			}
		}
	}

	/**
	 * 获取文件的Uri
	 * 
	 * @param path
	 *            文件的路径
	 * @return
	 */
	public static Uri getUriFromFile(String path) {
		File file = new File(path);
		return Uri.fromFile(file);
	}

	/**
	 * 换算文件大小
	 * 
	 * @param size
	 * @return
	 */
	public static String formatFileSize(long size) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "未知大小";
		if (size < 1024) {
			fileSizeString = df.format((double) size) + "B";
		} else if (size < 1048576) {
			fileSizeString = df.format((double) size / 1024) + "K";
		} else if (size < 1073741824) {
			fileSizeString = df.format((double) size / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) size / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName, InputStream input, long size) {
		File file = null;
		OutputStream output = null;
		long onePercentage = size / 100;
		int progress = 0;

		try {
			createDirFile(path);
			file = createNewFile(path + fileName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			output = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			int length = 0;
			long readCount = 0;
			while ((length = input.read(buffer)) != -1) {
				output.write(buffer, 0, length);
				readCount += length;
				long maxProgress = readCount / onePercentage;
				if (maxProgress > progress && maxProgress % 5 == 0) {
					progress = (int) maxProgress;
					Intent intent = new Intent(NotificationReceiver.ACTION_NOTIFICATION_SHOW_DOWNLOAD_APP);
					intent.putExtra(NotificationReceiver.EXTRA_DOWNLOAD_PROGRESS, progress);
					intent.putExtra(NotificationReceiver.EXTRA_DOWNLOAD_STATE,
							NotificationReceiver.DOWNLOAD_SATAE_DOWNLOADING);
					TraineeApplication.CONTEXT.sendBroadcast(intent);
				}
			}
			output.flush();
		} catch (Exception e) {
			file = null;
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
