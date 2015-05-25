package com.xhr88.lp.activity;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.PayAdapter;
import com.xhr88.lp.adapter.TaskAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.TaskReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.PayModel;
import com.xhr88.lp.model.datamodel.TaskModel;
import com.xhr88.lp.model.viewmodel.MyCoinModel;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.widget.CustomDialog.Builder;
import com.xhr88.lp.widget.MListView;

/**
 * 我的资产界面
 * 
 * @author zou.sq
 */
public class MyAssetsActivity extends TraineeBaseActivity implements OnClickListener {

	private static final int GET_HELP_LIST_FAIL = 0;
	private static final int GET_HELP_LIST_SUCCESS = 1;
	private static final int DIALOG_SIGN_SUCCESS = 2;

	private LoadingUpView mLoadingUpView;
	private boolean mIsGetSearchList;
	private boolean mIsReceiveTask;
	private MListView mTaskListview;
	private TaskAdapter mTaskAdapter;
	private ArrayList<TaskModel> mTaskModels;
	private MListView mPayListview;
	private PayAdapter mPayAdapter;
	private TextView mTvCoin;
	private ArrayList<PayModel> mPayModels;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dismissLoadingUpView(mLoadingUpView);
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case GET_HELP_LIST_SUCCESS:
					MyCoinModel model = (MyCoinModel) result.ResultObject;
					if (null != model && null != model.listTask) {
						mTaskModels.addAll(model.listTask);
						mTaskAdapter.notifyDataSetChanged();
					}
					if (null != model && null != model.listPay) {
						mPayModels.addAll(model.listPay);
						mPayAdapter.notifyDataSetChanged();
					}
					break;
				case GET_HELP_LIST_FAIL:
					break;
				default:
					break;
			}
			mIsGetSearchList = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_assets);
		initVariables();
		initViews();
		getTaskList();
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this, true);
		mTaskModels = new ArrayList<TaskModel>();
		mTaskAdapter = new TaskAdapter(this, mTaskModels, this);
		mPayModels = new ArrayList<PayModel>();
		mPayAdapter = new PayAdapter(this, mPayModels, this);
	}

	private void initViews() {
		mTaskListview = (MListView) findViewById(R.id.lv_my_task);
		mTaskListview.setAdapter(mTaskAdapter);
		mPayListview = (MListView) findViewById(R.id.lv_my_buy_list);
		mPayListview.setAdapter(mPayAdapter);
		mTvCoin = (TextView) findViewById(R.id.tv_coin);
	}

	@Override
	public void onResume() {
		if (UserMgr.hasUserInfo()) {
			UserViewModel userInfo = UserDao.getLocalUserInfo();
			mTvCoin.setText(userInfo.UserInfo.getCoin() + "");
		}
		super.onResume();
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.rl_my_coin_item:
				startActivity(new Intent(MyAssetsActivity.this, MyPayRecordActivity.class));
				break;
			case R.id.btn_sign:
				TaskModel model = (TaskModel) view.getTag();
				if (null != model && model.getTaskid() > 0) {
					receiveTask(model.getTaskid());
				}
				break;
			case R.id.btn_buy:
				PayModel payModel = (PayModel) view.getTag();
				if (null != payModel) {
					Intent intent = new Intent(MyAssetsActivity.this, OnlinePayActivity.class);
					intent.putExtra(PayModel.class.getName(), payModel);
					startActivity(intent);
				}
				break;

			default:
				break;
		}
	}

	private void receiveTask(final int taskid) {
		if (mIsReceiveTask) {
			return;
		}
		mIsReceiveTask = true;
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(MyAssetsActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				mIsReceiveTask = false;
				dismissLoadingUpView(mLoadingUpView);
				if (1 == taskid) {
					showDialog(DIALOG_SIGN_SUCCESS);
				} else if (2 == taskid) {
					toast("领取成功！只有第一次分享才有奖励！");
				}
				for (int i = 0; i < mTaskModels.size(); i++) {
					TaskModel model = mTaskModels.get(i);
					if (taskid == model.getTaskid()) {
						model.setGetstatus(1);
						if (UserMgr.hasUserInfo()) {
							UserViewModel userInfo = UserDao.getLocalUserInfo();
							int currentCoin = userInfo.UserInfo.getCoin() + model.getCoin();
							userInfo.UserInfo.setCoin(currentCoin);
							UserDao.saveLocalUserInfo(userInfo);
							mTvCoin.setText(currentCoin + "");
						}
						mTaskAdapter.notifyDataSetChanged();
						break;
					}
				}
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				mIsReceiveTask = false;
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TaskReq.receiveTask(taskid + "");
			}
		});
	}

	private void getTaskList() {
		if (mIsGetSearchList) {
			return;
		}
		mIsGetSearchList = true;
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(MyAssetsActivity.this, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				sendMessageToHandler(GET_HELP_LIST_SUCCESS, result);
			}

			@Override
			public void onError(ActionResult result) {
				sendMessageToHandler(GET_HELP_LIST_FAIL, result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TaskReq.getTaskList();
			}
		});
	}

	private void sendMessageToHandler(int what, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if (DIALOG_SIGN_SUCCESS == id) {
			Builder builder = createDialogBuilder(this, "", "", "关闭", "");
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			iv.setImageResource(R.drawable.icon_sign_success);
			builder.setmDialogView(iv);
			return builder.create(id);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	public void finish() {
		sendBroadcast(new Intent(ConstantSet.ACTION_REFREASH_COIN));
		super.finish();
	}

}
