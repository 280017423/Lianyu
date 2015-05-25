package com.xhr88.lp.listener;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

import com.xhr.framework.authentication.BaseLoginProcessor;
import com.xhr88.lp.activity.RegisterActivity;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.util.DialogManager;
import com.xhr88.lp.widget.CustomDialog.Builder;

public class ForceOfflineReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (null == intent) {
			return;
		}
		String message = intent.getStringExtra("content");
		Builder builder = DialogManager.createMessageDialogBuilder(context, "警告", message, "确定", "",
				new IDialogProtocol() {

					@Override
					public void onPositiveBtnClick(int id, DialogInterface dialog, int which) {
						NotificationManager nm = (NotificationManager) context
								.getSystemService(Context.NOTIFICATION_SERVICE);
						nm.cancelAll();
						UserMgr.logout();
						Intent intent = new Intent(context, RegisterActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(BaseLoginProcessor.KEY_LOGIN_TYPE, ConstantSet.EXIT_TO_CANCEL_APK);
						context.startActivity(intent);
					}

					@Override
					public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
					}

					@Override
					public Builder createDialogBuilder(Context context, String title, String message,
							String positiveBtnName, String negativeBtnName) {
						return null;
					}
				});
		Dialog alterDialog = builder.create(1);
		alterDialog.setCancelable(false);
		// 添加对话框类型：保证在广播中正常弹出
		alterDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		// 对话框展示
		alterDialog.show();
	}

}
