package com.xhr88.lp.activity;

import io.rong.imkit.RongIM;

import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xhr.framework.util.HttpClientUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.business.request.SystemReq;
import com.xhr88.lp.fragment.ActivityFragment;
import com.xhr88.lp.fragment.ConversationListFragment;
import com.xhr88.lp.fragment.MyCenterFragment;
import com.xhr88.lp.fragment.RecommendFragment;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.listener.IDialogProtocol;
import com.xhr88.lp.model.datamodel.ConfigModel;
import com.xhr88.lp.model.datamodel.UpdateModel;
import com.xhr88.lp.util.DialogManager;
import com.xhr88.lp.util.RongIMUtil;
import com.xhr88.lp.widget.CustomDialog.Builder;

/**
 * @author huang.b
 * @data 2014-8-13
 */
public class MainActivity extends FragmentBaseActivity implements OnClickListener {

	public static final String ACTION_DMEO_RECEIVE_MESSAGE = "action_demo_receive_message";
	public static final String ACTION_RECEIVE_NEW_TRENDS = "action_receive_new_trends";
	public static final String ACTION_FINISH_ALL = "action_finish_all";
	public static final String ACTION_DOWONLOAD_FINISH = "action_dowonload_finish";
	private static final int DIALOG_UPDATE = 101;
	public static final int LOGIN_REQUEST_CODE = 111;
	public static int mTempJumpToIndex = -1;
	public static MainActivity INSTANCE;
	private int mCurrentTabIndex;
	private int mJumpToTabIndex;
	private FragmentManager fragmentManager;
	private RecommendFragment mHomeFragment;
	private MyCenterFragment mMyCenterFragment;
	private ActivityFragment mActivityFragment;
	private ConversationListFragment mMessageFragment;
	private View mViewNewMsg;
	private View mViewNewTrends;
	private View mTitleRight;
	private TextView mTvTitle;
	public ImageView mIVNewMessageHint;
	public int mJumpTo;
	private OnClickListener mClickListener;
	private ReceiveMessageBroadcastReciver mBroadcastReciver;
	private int numbermessage;
	public boolean isNewMessage;
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntentFilter.addAction(ACTION_EXIT_APP);
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.activity_main);
		initVariable();
		initViews();
		connectRongYun();
		setTabSelection(mJumpToTabIndex, true);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_DMEO_RECEIVE_MESSAGE);
		intentFilter.addAction(ACTION_RECEIVE_NEW_TRENDS);
		intentFilter.addAction(ACTION_FINISH_ALL);
		intentFilter.addAction(ACTION_DOWONLOAD_FINISH);
		if (mBroadcastReciver == null) {
			mBroadcastReciver = new ReceiveMessageBroadcastReciver();
		}
		registerReceiver(mBroadcastReciver, intentFilter);
		checkUpdate();
	}

	private class ReceiveMessageBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_DMEO_RECEIVE_MESSAGE)) {
				numbermessage = intent.getIntExtra("rongCloud", -1);
				if (RongIM.getInstance() != null) {
					try {
						numbermessage = RongIM.getInstance().getTotalUnreadCount();
						if (numbermessage > 0) {
							UIUtil.setViewVisible(mViewNewMsg);
						} else {
							UIUtil.setViewGone(mViewNewMsg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (action.equals(ACTION_RECEIVE_NEW_TRENDS)) {
				boolean hasNewTrends = intent.getBooleanExtra("hasNewTrends", false);
				if (hasNewTrends) {
					UIUtil.setViewVisible(mViewNewTrends);
				} else {
					UIUtil.setViewGone(mViewNewTrends);
				}
			} else if (action.equals(ACTION_FINISH_ALL)) {
				Intent intent1 = new Intent(MainActivity.this, MainActivity.class);
				intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);
				MainActivity.INSTANCE.finish();
			} else if (action.equals(ACTION_DOWONLOAD_FINISH)) {
				HttpClientUtil.setCookieStore(null);
				finish();
			}
		}
	}

	private void connectRongYun() {
		try {
			RongIMUtil.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mTempJumpToIndex != -1) {
			setTabSelection(mTempJumpToIndex, false);
		}
		if (RongIM.getInstance() != null) {
			try {
				numbermessage = RongIM.getInstance().getTotalUnreadCount();
				if (numbermessage > 0) {
					UIUtil.setViewVisible(mViewNewMsg);
				} else {
					UIUtil.setViewGone(mViewNewMsg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 检查强制更新
	private void checkUpdate() {
		List<ConfigModel> configModels = DBMgr.getBaseModel(ConfigModel.class);
		if (configModels == null || configModels.size() == 0 || configModels.get(0).getMustupdate() != 1) {
			return;
		}
		checkVersion();
	}

	UpdateModel mModel;

	private void checkVersion() {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				mModel = (UpdateModel) result.ResultObject;
				if (null != mModel) {
					showDialog(DIALOG_UPDATE);
				}
			}

			@Override
			public void onError(ActionResult result) {
			}

			@Override
			public ActionResult onAsyncRun() {
				return SystemReq.checkUpdate();
			}
		});

	}

	private void initVariable() {
		INSTANCE = this;
		fragmentManager = this.getSupportFragmentManager();
		mJumpToTabIndex = TabHomeIndex.HOME_RECOMMEND;
	}

	private void initViews() {
		mTitleRight = findViewById(R.id.title_with_back_title_btn_right);
		mTvTitle = (TextView) findViewById(R.id.tv_main_title);
		mViewNewMsg = findViewById(R.id.iv_new_message_icon);
		mViewNewTrends = findViewById(R.id.iv_new_trends_icon);
	}

	public void addOnClicListener(OnClickListener listener) {
		mClickListener = listener;
	}

	/**
	 * 切换到指定的Fragment
	 * 
	 * @param viewId
	 */
	public void setTabSelection(int tabHomeIndex, boolean isFromOncreate) {
		if (mCurrentTabIndex == tabHomeIndex) {
			return;
		}
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 设置切换动画
		// transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (tabHomeIndex) {
			case TabHomeIndex.HOME_RECOMMEND:
				UIUtil.setViewVisible(mTitleRight);
				mTvTitle.setText("推荐");
				if (mHomeFragment == null) {
					// 如果mDiscoverFragment为空，则创建一个并添加到界面上
					mHomeFragment = RecommendFragment.newInstance();
					transaction.add(R.id.content, mHomeFragment);
				} else {
					// 如果mDiscoverFragment不为空，则直接将它显示出来
					transaction.show(mHomeFragment);
				}
				this.setHomeClickedStatus();
				break;
			case TabHomeIndex.HOME_MESSAGE:
				mTvTitle.setText("消息");
				UIUtil.setViewGone(mTitleRight);
				if (mMessageFragment == null) {
					// 如果mDiscoverFragment为空，则创建一个并添加到界面上
					mMessageFragment = new ConversationListFragment();
					// mMessageFragment = new MessageFragment();
					transaction.add(R.id.content, mMessageFragment);
				} else {
					// 如果mDiscoverFragment不为空，则直接将它显示出来
					transaction.show(mMessageFragment);
				}
				this.setMessageClickedStatus();
				break;
			case TabHomeIndex.HOME_ACTIVITY:
				mTvTitle.setText("活动");
				UIUtil.setViewGone(mTitleRight);
				if (mActivityFragment == null) {
					// 如果mDiscoverFragment为空，则创建一个并添加到界面上
					mActivityFragment = new ActivityFragment();
					transaction.add(R.id.content, mActivityFragment, "huodong");
				} else {
					transaction.show(mActivityFragment);
				}
				setActivityClickedStatus();
				break;
			case TabHomeIndex.HOME_MY_CENTER:
				mTvTitle.setText("我的");
				UIUtil.setViewGone(mTitleRight);
				if (mMyCenterFragment == null) {
					// 如果mDiscoverFragment为空，则创建一个并添加到界面上
					mMyCenterFragment = new MyCenterFragment();
					transaction.add(R.id.content, mMyCenterFragment, "geren");
				} else {
					mMyCenterFragment.loadUserInfo();
					transaction.show(mMyCenterFragment);
				}
				setMyCenterClickedStatus();
				break;
			default:

				break;
		}
		mCurrentTabIndex = tabHomeIndex;
		if (!isFromOncreate) {
			mTempJumpToIndex = -1;
		}
		// 当activity再次被恢复时commit之后的状态将丢失,所以这里不能用commit
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
	 * 
	 * @param transaction
	 */
	private void hideFragments(FragmentTransaction transaction) {
		List<Fragment> list = fragmentManager.getFragments();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				transaction.hide(list.get(i));
			}
		}
		setHomeUnClickedStatus();
		setMyCenterUnClickedStatus();
		setActivityUnClickedStatus();
		setMessageUnClickedStatus();
	}

	public static class TabHomeIndex {
		public static final int HOME_RECOMMEND = 1; // 推荐首页
		public static final int HOME_MESSAGE = 2; // 消息
		public static final int HOME_ACTIVITY = 3; // 活动
		public static final int HOME_MY_CENTER = 4; // "我的"首页
	}

	private void setHomeUnClickedStatus() {
		ImageView homeImage = (ImageView) this.findViewById(R.id.img_index_icon);
		TextView homeText = (TextView) this.findViewById(R.id.txt_home);
		homeText.setTextColor(getResources().getColor(R.color.tab_text_color));
		homeImage.setImageDrawable(getResources().getDrawable(R.drawable.home_selecter_1_normal));
	}

	private void setHomeClickedStatus() {
		ImageView homeImage = (ImageView) this.findViewById(R.id.img_index_icon);
		TextView homeText = (TextView) this.findViewById(R.id.txt_home);
		homeText.setTextColor(getResources().getColor(R.color.tab_select_text_color));
		homeImage.setImageDrawable(getResources().getDrawable(R.drawable.home_selecter_1_press));
	}

	private void setActivityUnClickedStatus() {
		ImageView homeImage = (ImageView) this.findViewById(R.id.img_activity_icon);
		TextView homeText = (TextView) this.findViewById(R.id.txt_activity);
		homeText.setTextColor(getResources().getColor(R.color.tab_text_color));
		homeImage.setImageDrawable(getResources().getDrawable(R.drawable.home_selecter_3_normal));
	}

	private void setActivityClickedStatus() {
		ImageView homeImage = (ImageView) this.findViewById(R.id.img_activity_icon);
		TextView homeText = (TextView) this.findViewById(R.id.txt_activity);
		homeText.setTextColor(getResources().getColor(R.color.tab_select_text_color));
		homeImage.setImageDrawable(getResources().getDrawable(R.drawable.home_selecter_3_press));
	}

	private void setMyCenterUnClickedStatus() {
		ImageView homeImage = (ImageView) this.findViewById(R.id.img_geren_icon);
		TextView homeText = (TextView) this.findViewById(R.id.txt_geren);
		homeText.setTextColor(getResources().getColor(R.color.tab_text_color));
		homeImage.setImageDrawable(getResources().getDrawable(R.drawable.home_selecter_4_normal));
	}

	private void setMyCenterClickedStatus() {
		ImageView homeImage = (ImageView) this.findViewById(R.id.img_geren_icon);
		TextView homeText = (TextView) this.findViewById(R.id.txt_geren);
		homeText.setTextColor(getResources().getColor(R.color.tab_select_text_color));
		homeImage.setImageDrawable(getResources().getDrawable(R.drawable.home_selecter_4_press));
	}

	private void setMessageUnClickedStatus() {
		ImageView homeImage = (ImageView) this.findViewById(R.id.img_message_icon);
		TextView homeText = (TextView) this.findViewById(R.id.txt_message);
		homeText.setTextColor(getResources().getColor(R.color.tab_text_color));
		homeImage.setImageDrawable(getResources().getDrawable(R.drawable.home_selecter_2_normal));
	}

	private void setMessageClickedStatus() {
		ImageView homeImage = (ImageView) this.findViewById(R.id.img_message_icon);
		TextView homeText = (TextView) this.findViewById(R.id.txt_message);
		homeText.setTextColor(getResources().getColor(R.color.tab_select_text_color));
		homeImage.setImageDrawable(getResources().getDrawable(R.drawable.home_selecter_2_press));
	}

	@Override
	protected void processBroadReceiver(String action, Object data) {
		if (ACTION_EXIT_APP.equals(action) && !isFinishing()) {
			finish();
			return;
		}
		super.processBroadReceiver(action, data);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_UPDATE:
				Dialog dialog = DialogManager.createMessageDialogBuilder(this, "发现新版本" + mModel.getUpdatever(),
						mModel.getVerinfo().replaceAll("-", "\n"), getString(R.string.button_text_update),
						getString(R.string.button_text_exit), new IDialogProtocol() {

							@Override
							public void onPositiveBtnClick(int id, DialogInterface dialog, int which) {
								Intent intent = new Intent(MainActivity.this, UpdateStateActivity.class);
								intent.putExtra(UpdateModel.class.getName(), mModel);
								startActivity(intent);
							}

							@Override
							public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
								HttpClientUtil.setCookieStore(null);
								finish();
							}

							@Override
							public Builder createDialogBuilder(Context context, String title, String message,
									String positiveBtnName, String negativeBtnName) {
								return null;
							}
						}).create(id);
				dialog.setCancelable(false);
				return dialog;
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				toast("再按一次退出程序");
				mExitTime = System.currentTimeMillis();
			} else {
				MobclickAgent.onKillProcess(MainActivity.this);
				HttpClientUtil.setCookieStore(null);
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_home_tuijian_layout:
				setTabSelection(TabHomeIndex.HOME_RECOMMEND, false);
				break;
			case R.id.rl_home_message_layout:
				setTabSelection(TabHomeIndex.HOME_MESSAGE, false);
				break;
			case R.id.rl_home_activity_layout:
				setTabSelection(TabHomeIndex.HOME_ACTIVITY, false);
				break;
			case R.id.rl_home_my_layout:
				setTabSelection(TabHomeIndex.HOME_MY_CENTER, false);
				break;
			case R.id.iv_search:
				startActivity(new Intent(MainActivity.this, SearchActivity.class));
				break;
			case R.id.btn_filter:
				if (null != mClickListener) {
					mClickListener.onClick(v);
				}
				break;

			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		if (mBroadcastReciver != null) {
			this.unregisterReceiver(mBroadcastReciver);
		}
		super.onDestroy();
	}
}
