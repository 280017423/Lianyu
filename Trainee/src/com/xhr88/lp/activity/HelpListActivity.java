package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshListView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.HelpListAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.SystemReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.HelpListModel;
import com.xhr88.lp.model.datamodel.RecommendListModel;

/**
 * 帮助列表界面
 * 
 * @author zou.sq
 */
public class HelpListActivity extends TraineeBaseActivity implements OnClickListener, OnItemClickListener {

	private static final int GET_HELP_LIST_FAIL = 0;
	private static final int GET_HELP_LIST_SUCCESS = 1;
	private static final int PULL_DOWN = 0;
	private static final int PULL_UP = 1;

	private List<HelpListModel> mHelpListModels;
	private HelpListAdapter mHelpListAdapter;
	private PullToRefreshListView mPullToRefreshListView;
	private int mCurrentPage;
	private boolean mIsGetSearchList;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case GET_HELP_LIST_SUCCESS:
					List<HelpListModel> models = (List<HelpListModel>) result.ResultObject;
					mPullToRefreshListView.onRefreshComplete();
					mCurrentPage++;
					if (PULL_DOWN == msg.arg1) {
						mHelpListModels.clear();
					}
					if (null != models && models.size() < ConstantSet.PAGE_SIZE) {
						mPullToRefreshListView.setNoMoreData();
					}
					showData(result, true);
					break;
				case GET_HELP_LIST_FAIL:
					if (mHelpListModels.isEmpty()) {
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
		List<HelpListModel> models = (List<HelpListModel>) result.ResultObject;
		if (null != models) {
			mHelpListModels.addAll(models);
		}
		if (mHelpListAdapter != null) {
			mHelpListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_list);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mHelpListModels = new ArrayList<HelpListModel>();
		mHelpListAdapter = new HelpListAdapter(this, mHelpListModels);
	}

	private void initViews() {
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_help_list);
		ListView listView = mPullToRefreshListView.getRefreshableView();
		listView.setCacheColorHint(getResources().getColor(R.color.transparent));
		listView.setFadingEdgeLength(0);
		listView.setOnItemClickListener(this);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		HelpListModel model = (HelpListModel) parent.getAdapter().getItem(position);
		if (null != model) {
			Intent intent = new Intent(HelpListActivity.this, HelpDetailActivity.class);
			intent.putExtra(HelpListModel.class.getName(), model);
			startActivity(intent);
		}
	}

	private void getHelpList(final int status) {
		if (mIsGetSearchList) {
			return;
		}
		mIsGetSearchList = true;
		new ActionProcessor().startAction(HelpListActivity.this, new IActionListener() {

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
				return SystemReq.getHelpList(mCurrentPage);
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
