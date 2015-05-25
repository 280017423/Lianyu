package com.xhr88.lp.update;


import java.io.File;

import com.xhr88.lp.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;


/**
 * 通知统一管理
 * @author wu.zg
 */
public class NotificationReceiver extends BroadcastReceiver {


	public static final String ACTION_NOTIFICATION_SHOW = "action_xhr_notification_show";
	public static final String ACTION_NOTIFICATION_HIDE = "action_xhr_notification_hide";
	public static final String ACTION_NOTIFICATION_SHOW_DOWNLOAD_APP = "action_xhr_notification_show_download_app";
	public static final String ACTION_NOTIFICATION_HIDE_DOWNLOAD_APP = "action_xhr_notification_hide_download_app";

	public static final String EXTRA_DATA = "extra_data";
	public static final String EXTRA_ID = "extra_id";

	/**
	 * 下载进度，整数0-100之间
	 */
	public static final String EXTRA_DOWNLOAD_PROGRESS = "extra_download_progress";
	public static final String EXTRA_DOWNLOAD_STATE = "extra_download_state";
	public static final String EXTRA_DOWNLOAD_FILEPATH = "extra_download_filepath";

	public static final int DOWNLOAD_SATAE_INIT = 0X3151;
	public static final int DOWNLOAD_SATAE_DOWNLOADING = 0X3152;
	public static final int DOWNLOAD_SATAE_FINISH = 0X3153;
	public static final int DOWNLOAD_SATAE_FAILED = 0X3154;

	public static final int ID_NOLOGIN = 0;
	public static final int ID_MAIL = 1;
	public static final int ID_RECOMMOND = 2;
	public static final int ID_DOWNLOAD_APP = 0X3150;

	private static NotificationManager mNotificationManager = null;

	private Notification downloadNotification = null;

	public void onReceive(Context context, Intent intent) {
		
		if (intent == null) {
			return;
		}
		
		//非本程序发出的通知不处理
//		if (!CommonTool.getPackageName().equals(intent.getStringExtra(CookieKeyConstants.app))) {
//			return;
//		}
		
		String action = intent.getAction();
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (ACTION_NOTIFICATION_HIDE.equals(action)) {
			//隐藏通知
			int id = intent.getIntExtra(EXTRA_ID, -1);
			if (id >= 0) {
				mNotificationManager.cancel(id);
			}
		} else if (ACTION_NOTIFICATION_SHOW_DOWNLOAD_APP.equals(action)) {
			if (downloadNotification == null) {
				downloadNotification = new Notification();
				downloadNotification.flags = Notification.FLAG_ONGOING_EVENT;
				downloadNotification.icon = R.drawable.stat_sys_download;
				downloadNotification.tickerText = context.getString(R.string.UMDownload_tickerText);
				downloadNotification.contentView = new RemoteViews(context.getApplicationContext().getPackageName(),
						R.layout.update_download_notification);
			}
			int downloadState = intent.getIntExtra(EXTRA_DOWNLOAD_STATE, -1);
			if (downloadState == -1) {
				return;
			}
			downloadNotification.contentIntent = PendingIntent.getActivity(context, 0, new Intent(),
					PendingIntent.FLAG_UPDATE_CURRENT);
			switch (downloadState) {
			case DOWNLOAD_SATAE_INIT:
				downloadNotification.contentView.setProgressBar(R.id.umeng_common_progress_bar, 100, 0, false);
				downloadNotification.contentView.setTextViewText(R.id.umeng_common_progress_text, "0%");
				downloadNotification.contentView.setTextViewText(R.id.umeng_common_title,context.getString(R.string.UMDownload_tickerText));
				break;
			case DOWNLOAD_SATAE_DOWNLOADING:
				int progress = intent.getIntExtra(EXTRA_DOWNLOAD_PROGRESS, 0);
				if (progress > 100) { 
					progress = 100;
				}
				downloadNotification.contentView.setProgressBar(R.id.umeng_common_progress_bar, 100, progress, false);
				downloadNotification.contentView.setTextViewText(R.id.umeng_common_progress_text, progress + "%");
				downloadNotification.contentView.setTextViewText(R.id.umeng_common_title,
						context.getString(R.string.UMDownload_tickerText));
				break;
			case DOWNLOAD_SATAE_FINISH:
				mNotificationManager.cancel(ID_DOWNLOAD_APP);
				
				String filePath = intent.getStringExtra(EXTRA_DOWNLOAD_FILEPATH);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				installIntent.setDataAndType(Uri.fromFile(new File(filePath)),"application/vnd.android.package-archive");
				context.startActivity(installIntent);
				return;

			case DOWNLOAD_SATAE_FAILED:
				String failTitle = context.getString(R.string.app_name) + " " + context.getString(R.string.UMDownload_faild);
				downloadNotification.flags = Notification.FLAG_AUTO_CANCEL;
				downloadNotification.tickerText = failTitle;
				downloadNotification.setLatestEventInfo(context, failTitle, failTitle,
						PendingIntent.getActivity(context, 0, new Intent(),
								PendingIntent.FLAG_UPDATE_CURRENT));

				break;
			}
			mNotificationManager.notify(ID_DOWNLOAD_APP, downloadNotification);
		} else if (ACTION_NOTIFICATION_HIDE_DOWNLOAD_APP.equals(action)) {
			mNotificationManager.cancel(ID_DOWNLOAD_APP);
			downloadNotification = null;
		}

	}

//	/**
//	 * 隐藏notification
//	 * @param key notification的key
//	 */
//	public static final void hideNotification(int key) {
//		Intent intent = new Intent(ACTION_NOTIFICATION_HIDE);
//		intent.putExtra(EXTRA_ID, key);
//		intent.putExtra(CookieKeyConstants.app, CommonTool.getPackageName());
//		YuanLai.appContext.sendBroadcast(intent);
//	}
//
//	/**
//	 * 隐藏全部图标
//	 */
//	public static final void hideAllNotification() {
//		hideNotification(ID_NOLOGIN);
//		hideNotification(ID_MAIL);
//		hideNotification(ID_RECOMMOND);
//	}

}
