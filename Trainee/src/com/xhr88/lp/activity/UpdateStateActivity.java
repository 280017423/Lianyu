package com.xhr88.lp.activity;

import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.UpdateModel;
import com.xhr88.lp.update.DownloadFileThread;
import com.xhr88.lp.update.NotificationReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author wu.zg 强制升级
 * */
public class UpdateStateActivity extends TraineeBaseActivity {

	TextView mTxtProgress, mTxtState;
	ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_download);
		getWindow().getDecorView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 什么都不做，避免点击到区域外时页面关闭
				return true;
			}
		});
		registerReceiver(mReceiver, new IntentFilter(NotificationReceiver.ACTION_NOTIFICATION_SHOW_DOWNLOAD_APP));
		initViews();
		// 开始下载更新
		UpdateModel model = (UpdateModel) getIntent().getSerializableExtra(UpdateModel.class.getName());
		new DownloadFileThread(model).start();
	}

	private void initViews() {
		mTxtProgress = (TextView) findViewById(R.id.umeng_common_progress_text);
		mTxtState = (TextView) findViewById(R.id.umeng_common_title);
		mProgressBar = (ProgressBar) findViewById(R.id.umeng_common_progress_bar);

	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (NotificationReceiver.ACTION_NOTIFICATION_SHOW_DOWNLOAD_APP.equals(action)) {
				int downloadState = intent.getIntExtra(NotificationReceiver.EXTRA_DOWNLOAD_STATE, -1);
				if (downloadState == -1) {
					return;
				}
				switch (downloadState) {
					case NotificationReceiver.DOWNLOAD_SATAE_INIT:
						break;
					case NotificationReceiver.DOWNLOAD_SATAE_DOWNLOADING:
						int progress = intent.getIntExtra(NotificationReceiver.EXTRA_DOWNLOAD_PROGRESS, 0);
						if (progress > 100) {
							progress = 100;
						}
						mProgressBar.setProgress(progress);
						mTxtProgress.setText(progress + "%");
						mTxtState.setText(getString(R.string.UMDownload_tickerText));
						break;
					case NotificationReceiver.DOWNLOAD_SATAE_FINISH:
						sendBroadcast(new Intent(MainActivity.ACTION_DOWONLOAD_FINISH));
						finish();
						return;

					case NotificationReceiver.DOWNLOAD_SATAE_FAILED:
						mTxtState.setText(getString(R.string.UMDownload_faild));

						break;
				}
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
		super.onDestroy();
	}
}
