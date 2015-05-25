package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshListView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.HistoryPayListAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.TaskReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.HistoryPayModel;
import com.xhr88.lp.model.datamodel.RecommendListModel;
import com.xhr88.lp.util.ViewUtil;

/**
 * 我的历史交易界面
 * 
 * @author zou.sq
 */
public class MyPayRecordActivity extends TraineeBaseActivity implements OnClickListener {

	private static final int GET_HELP_LIST_FAIL = 0;
	private static final int GET_HELP_LIST_SUCCESS = 1;
	private static final int PULL_DOWN = 0;
	private static final int PULL_UP = 1;

	private List<HistoryPayModel> mPayRecordList;
	private HistoryPayListAdapter mHelpListAdapter;
	private PullToRefreshListView mPullToRefreshListView;
	private int mCurrentPage;
	private boolean mIsGetSearchList;
	private View mEmptyView;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case GET_HELP_LIST_SUCCESS:
					mPullToRefreshListView.onRefreshComplete();
					List<HistoryPayModel> models = (List<HistoryPayModel>) result.ResultObject;
					mCurrentPage++;
					if (PULL_DOWN == msg.arg1) {
						mPayRecordList.clear();
					}
					if (null != models && models.size() < ConstantSet.PAGE_SIZE) {
						mPullToRefreshListView.setNoMoreData();
						mPullToRefreshListView.setMode(Mode.PULL_DOWN_TO_REFRESH);
					} else {
						mPullToRefreshListView.setMode(Mode.BOTH);
					}
					showData(result, true);
					if (null != mPayRecordList && mPayRecordList.isEmpty()) {
						mPullToRefreshListView.setEmptyView(mEmptyView);
					}
					break;
				case GET_HELP_LIST_FAIL:
					if (mPayRecordList.isEmpty()) {
						// 当前列表无数据，网络异常并且本里无缓存数据，那么显示错误界面
						if (result.ResultCode != null && ActionResult.RESULT_CODE_NET_ERROR.equals(result.ResultCode)
								&& result.ResultObject != null
								&& !((List<RecommendListModel>) result.ResultObject).isEmpty()) {
							showData(result, false);
						}
					} else {
						showErrorMsg(result);
					}
					mPullToRefreshListView.onRefreshComplete();
					break;
				default:
					break;
			}
			mIsGetSearchList = false;
		}
	};

	protected void showData(ActionResult result, boolean isSuccess) {
		List<HistoryPayModel> models = (List<HistoryPayModel>) result.ResultObject;
		if (null != models) {
			mPayRecordList.addAll(models);
		}
		if (mHelpListAdapter != null) {
			mHelpListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_pay_record);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mPayRecordList = new ArrayList<HistoryPayModel>();
		mHelpListAdapter = new HistoryPayListAdapter(this, mPayRecordList);
	}

	private void initViews() {
		mEmptyView = ViewUtil.getEmptyView(this, getString(R.string.empty_info_pay_record));
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_history_pay_list);
		ListView listView = mPullToRefreshListView.getRefreshableView();
		listView.setCacheColorHint(getResources().getColor(R.color.transparent));
		listView.setFadingEdgeLength(0);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh() {
				mCurrentPage = 0;
				getHelpList(PULL_DOWN);
			}

			@Override
			public void onPullUpToRefresh() {
				getHelpList(PULL_UP);
			}
		});
		mPullToRefreshListView.setIsShowHeaderFresh(true);
		mPullToRefreshListView.setHeaderVisible(true);
		mPullToRefreshListView.setIsShowHeaderFresh(true);
		mPullToRefreshListView.setMode(Mode.BOTH);
		listView.setAdapter(mHelpListAdapter);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			default:
				break;
		}
	}

	private void getHelpList(final int status) {
		if (mIsGetSearchList) {
			return;
		}
		mIsGetSearchList = true;
		new ActionProcessor().startAction(MyPayRecordActivity.this, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				sendMessageToHandler(GET_HELP_LIST_SUCCESS, status, result);
			}

			@Override
			public void onError(ActionResult result) {
				sendMessageToHandler(GET_HELP_LIST_FAIL, status, result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TaskReq.getHistoryList(mCurrentPage);
			}
		});
	}

	private void sendMessageToHandler(int what, int status, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.arg1 = status;
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

}
