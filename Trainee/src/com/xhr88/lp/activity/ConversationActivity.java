package com.xhr88.lp.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.xhr.framework.app.XhrActivityBase.ActionListener;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.MainActivity.TabHomeIndex;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.MsgReq;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.ConversationUserInfoModel;
import com.xhr88.lp.model.datamodel.ServiceModel;
import com.xhr88.lp.util.DateUtil;
import com.xhr88.lp.util.TimerUtil;

public class ConversationActivity extends FragmentBaseActivity implements OnClickListener {

	private static final String TAG = "ConversationActivity";
	private String mUserId;
	private String mNickName;
	private TextView mTvTitle;
	private ServiceModel mCurrentModel;
	private int mCount;
	private View mRlServiceEnd;
	private View mRlServiceBuy;
	private LoadingUpView mLoadingUpView;
	private RatingBar mRatingBar;
	private PopupWindow mPopupWindow;
	private View mPopView;
	private TextView mTvTime;
	private TextView mTvScoreNum;
	private View mViewRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);
		initVariables();
		initViews();
		searchService(false);
		// 如果融云没有提供昵称，我们自己拿本地的。如果本地也没有，我们就去自己的服务器去取
		if (StringUtil.isNullOrEmpty(mNickName)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					ActionResult result = MsgReq.getUserInfo(mUserId);
					if (result.ResultCode.equals(ActionResult.RESULT_CODE_SUCCESS) && null != result) {
						ConversationUserInfoModel model = (ConversationUserInfoModel) result.ResultObject;
						if (null != model && !StringUtil.isNullOrEmpty(model.getUid())) {
							mNickName = model.getNickname();
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									mTvTitle.setText(mNickName + "对话");
								}
							});
						}
					}
				}
			}).start();
		}
	}

	private void initVariables() {
		Uri uri = getIntent().getData();
		mUserId = uri.getQueryParameter("targetId");
		mNickName = uri.getQueryParameter("title");
		EvtLog.d(TAG, "mUserId:" + mUserId + "mNickName:" + mNickName);
		// 如果融云没有提供昵称，我们自己拿本地的
		if (StringUtil.isNullOrEmpty(mNickName)) {
			ConversationUserInfoModel model = UserDao.getConversationUserInfoModel(mUserId);
			if (null != model && !StringUtil.isNullOrEmpty(model.getUid())) {
				mNickName = model.getNickname();
			}
		}
		mLoadingUpView = new LoadingUpView(this, true);
	}

	private void initViews() {
		mViewRight = findViewById(R.id.title_with_back_title_btn_right);
		mTvTitle = (TextView) findViewById(R.id.tv_title);
		if (!StringUtil.isNullOrEmpty(mNickName)) {
			mTvTitle.setText("与 " + mNickName + " 对话");
		}
		mRatingBar = (RatingBar) findViewById(R.id.service_ratingbar);
		mRlServiceEnd = findViewById(R.id.rl_service_rating);
		mRlServiceBuy = findViewById(R.id.rl_service_buy);
		mPopView = View.inflate(this, R.layout.view_service_pop, null);
		mTvTime = (TextView) mPopView.findViewById(R.id.tv_left_time);
		mTvScoreNum = (TextView) findViewById(R.id.tv_score_num);
		mPopupWindow = new PopupWindow(mPopView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setContentView(mPopView);
		mPopupWindow.setFocusable(true);
		// 点击popupwindow窗口之外的区域popupwindow消失
		ColorDrawable dw = new ColorDrawable(0x00);
		mPopupWindow.setBackgroundDrawable(dw);
		mPopupWindow.setOutsideTouchable(true);
		final Button btncomplain = (Button) mPopView.findViewById(R.id.btn_complain);
		btncomplain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				Intent intent = new Intent(ConversationActivity.this, ComplainActivity.class);
				intent.putExtra("targetId", mUserId);
				intent.putExtra("title", mNickName);
				startActivity(intent);
			}
		});
		mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				EvtLog.d("aaa", "rating:" + rating);
				mTvScoreNum.setText((int) rating + "分");
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				back();
				break;
			case R.id.btn_charge:
				Intent intent1 = new Intent(ConversationActivity.this, BuyServiceActivity.class);
				intent1.putExtra("touid", mUserId);
				intent1.putExtra("nickname", mNickName);
				startActivity(intent1);
				break;
			case R.id.btn_score_ok:
				if (null == mCurrentModel || StringUtil.isNullOrEmpty(mCurrentModel.getBid())) {
					return;
				}
				int rank = mRatingBar.getProgress();
				if (0 == rank) {
					toast("请选择评分");
				}
				serviceComment(rank + "");
				break;
			case R.id.iv_msg_info:
				if (null == mCurrentModel || StringUtil.isNullOrEmpty(mCurrentModel.getSid())) {
					searchService(true);
				} else {
					showServicePop();
				}
				break;

			default:
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void back() {
		MainActivity.INSTANCE.setTabSelection(TabHomeIndex.HOME_MESSAGE, false);
		Intent intent = new Intent(ConversationActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void searchService(final boolean isShow) {
		if (isShow) {
			showLoadingUpView(mLoadingUpView);
		}
		new ActionProcessor().startAction(ConversationActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				if (isShow) {
					dismissLoadingUpView(mLoadingUpView);
				}
				mCount = 0;
				if (null == result) {
					return;
				}
				mCurrentModel = (ServiceModel) result.ResultObject;
				// 如果我是卖方，直接正常显示
				if (UserMgr.isMineUid(mCurrentModel.getBuyuid())) {
					mCurrentModel.setType(2);
				}
				int type = mCurrentModel.getType();
				if (3 == type) {
					mRlServiceEnd.setVisibility(View.VISIBLE);
					if (mCurrentModel.getGrade() > 0) {
						mRatingBar.setRating((float) mCurrentModel.getGrade());
						mRlServiceEnd.setVisibility(View.GONE);
						mRlServiceBuy.setVisibility(View.VISIBLE);
						mRatingBar.setIsIndicator(true);
						EvtLog.d("aaa", "mRatingBar.getProgress():" + mRatingBar.getProgress());
						mTvScoreNum.setText(mRatingBar.getProgress() + "分");
					} else {
						mRlServiceEnd.setVisibility(View.VISIBLE);
						mRlServiceBuy.setVisibility(View.GONE);
						mRatingBar.setIsIndicator(false);
						mRatingBar.setRating(1f);
						mTvScoreNum.setText(mRatingBar.getProgress() + "分");
					}
				} else if (2 == type) {
					TimerUtil.startTimer(TAG, mCurrentModel.getEndtime(), 1000, new ActionListener() {

						@Override
						public void doAction() {
							ConversationActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (TimerUtil.getTimerTime(TAG) <= 0) {
										EvtLog.d(TAG, "倒计时结束");
										if (!UserMgr.isMineUid(mCurrentModel.getBuyuid())) {
											mRlServiceEnd.setVisibility(View.VISIBLE);
											mRlServiceBuy.setVisibility(View.GONE);
											mRatingBar.setIsIndicator(false);
											mRatingBar.setRating(1f);
											mTvScoreNum.setText(mRatingBar.getProgress() + "分");
										}
										TimerUtil.stopTimer(TAG);
									}
									mTvTime.setText(DateUtil.getLeftServiceTime(TimerUtil.getTimerTime(TAG)));
								}
							});
						}
					});
					if (isShow) {
						showServicePop();
					}
					mRlServiceEnd.setVisibility(View.GONE);
				} else if (4 == type) {
					mViewRight.setVisibility(View.GONE);
					mRlServiceEnd.setVisibility(View.GONE);
				} else {
					mRlServiceEnd.setVisibility(View.GONE);
					finish();
				}
			}

			@Override
			public void onError(ActionResult result) {
				if (isShow) {
					dismissLoadingUpView(mLoadingUpView);
					showErrorMsg(result);
				} else {
					mCount++;
					if (3 > mCount) {
						searchService(false);
					} else {
						mCount = 0;
					}
				}
			}

			@Override
			public ActionResult onAsyncRun() {
				return MsgReq.numSearch(mUserId);
			}
		});
	}

	private void showServicePop() {
		mPopupWindow.showAsDropDown(findViewById(R.id.iv_msg_info), 0, -12);
	}

	@Override
	protected void onDestroy() {
		TimerUtil.stopTimer(TAG);
		super.onDestroy();
	}

	private void serviceComment(final String rank) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(ConversationActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				mRlServiceEnd.setVisibility(View.GONE);
				mRlServiceBuy.setVisibility(View.VISIBLE);
				mRatingBar.setIsIndicator(true);
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return MsgReq.serviceComment(mCurrentModel.getBid(), rank);
			}
		});

	}
}
