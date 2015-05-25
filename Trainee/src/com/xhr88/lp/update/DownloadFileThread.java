package com.xhr88.lp.update;

import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.xhr88.lp.app.TraineeApplication;
import com.xhr88.lp.model.datamodel.UpdateModel;
import com.xhr88.lp.util.FileUtils;

/**
 * 下载升级文件线程
 * 
 * @author wuzg
 * 
 */
public class DownloadFileThread extends Thread {
	private UpdateModel mUpdateModel;
	// 是否正在下载应用
	public static boolean mIsDownload = false;
	private String mFilePath = null;

	public DownloadFileThread(UpdateModel mUpdateModel) {
		super("APP_UPGRADE_THREAD");
		this.mUpdateModel = mUpdateModel;
	}

	@Override
	public void run() {
		super.run();
		if (mUpdateModel == null) {
			return;
		}
		try {
			mIsDownload = true;
			sendDownloadState(NotificationReceiver.DOWNLOAD_SATAE_INIT, 0);
			String dirmFilePath = Environment.getExternalStorageDirectory() + "/xhr/com.xhr88.lp";
			File dirFile = new File(dirmFilePath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			String apkFileName = TraineeApplication.CONTEXT.getPackageName() + "_" + mUpdateModel.getUpdatever()
					+ ".apk";
			mFilePath = dirmFilePath + "/" + apkFileName;
			int result = downFile(mUpdateModel.getUpdateurl(), dirmFilePath + "/", apkFileName);
			sendDownloadState(
					result == 0 ? NotificationReceiver.DOWNLOAD_SATAE_FINISH : NotificationReceiver.DOWNLOAD_SATAE_FAILED,
					100);
		} catch (Exception e) {
			sendDownloadState(NotificationReceiver.DOWNLOAD_SATAE_FAILED, 0);
		} finally {
			mIsDownload = false;
		}

	}

	private void sendDownloadState(int state, int progress) {
		Intent showDownloadNotify = new Intent(NotificationReceiver.ACTION_NOTIFICATION_SHOW_DOWNLOAD_APP);
		showDownloadNotify.putExtra(NotificationReceiver.EXTRA_DOWNLOAD_STATE, state);
		showDownloadNotify.putExtra(NotificationReceiver.EXTRA_DOWNLOAD_PROGRESS, progress);
		showDownloadNotify.putExtra(NotificationReceiver.EXTRA_DOWNLOAD_FILEPATH, mFilePath);
		TraineeApplication.CONTEXT.sendBroadcast(showDownloadNotify);
	}

	/**
	 * 
	 * @param urlStr
	 * @param path
	 * @param fileName
	 * @return -1:文件下载出错 0:文件下载成功
	 */
	public int downFile(String urlStr, String path, String fileName) {
		InputStream inputStream = null;
		try {
			FileUtils fileUtils = new FileUtils();

			HttpURLConnection urlConn = null;
			long fileSize = 0;
			try {
				URL url = new URL(urlStr);
				urlConn = (HttpURLConnection) url.openConnection();
				inputStream = urlConn.getInputStream();

				// 计算默认文件大小
				fileSize = urlConn.getContentLength();

				// File file = new File(path + fileName);
				// // 如果之前下载过，且是完整的文件 ，直接用已有的文件
				// if (file != null && file.exists() && file.length() ==
				// fileSize) {
				// return 0;
				// }

			} catch (Exception e) {
				e.printStackTrace();
			}

			File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream, fileSize);
			if (resultFile == null) {
				return -1;
			}

		} catch (Exception e) {
			return -1;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
			}
		}
		return 0;
	}

}
