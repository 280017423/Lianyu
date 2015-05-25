package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshListView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.AttentionListAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.FansReq;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.AttentionModel;
import com.xhr88.lp.util.PopWindowUtil;
import com.xhr88.lp.util.ViewUtil;

public class MyAttentionActivity extends TraineeBaseActivity implements OnClickListener, OnItemClickListener {

	private static final int GET_FANS_LIST_FAIL = 0;
	private static final int GET_FANS_LIST_SUCCESS = 1;
	private static final int PULL_DOWN = 0;
	private static final int PULL_UP = 1;
	private PullToRefreshListView mPullToRefreshListView;
	private List<AttentionModel> mAttentionModelListModels;
	private AttentionListAdapter mAttentionListAdapter;
	private boolean mIsGetFansList;
	private int mCurrentPage;
	private View mEmptyView;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case GET_FANS_LIST_SUCCESS:
					mPullToRefreshListView.onRefreshComplete();
					List<AttentionModel> models = (List<AttentionModel>) result.ResultObject;
					mCurrentPage++;
					if (PULL_DOWN == msg.arg1) {
						mAttentionModelListModels.clear();
					}
					if (null != models && models.size() < ConstantSet.PAGE_SIZE) {
						mPullToRefreshListView.setNoMoreData();
					}
					showData(result, true);
					if (null != mAttentionModelListModels && mAttentionModelListModels.isEmpty()) {
						mPullToRefreshListView.setEmptyView(mEmptyView);
					}
					break;
				case GET_FANS_LIST_FAIL:
					if (mAttentionModelListModels.isEmpty()) {
						// 当前列表无数据，网络异常并且本里无缓存数据，那么显示错误界面
						if (result.ResultCode != null && ActionResult.RESULT_CODE_NET_ERROR.equals(result.ResultCode)
								&& result.ResultObject != null
								&& !((List<AttentionModel>) result.ResultObject).isEmpty()) {
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
			mIsGetFansList = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attention_list);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mAttentionModelListModels = new ArrayList<AttentionModel>();
		mAttentionListAdapter = new AttentionListAdapter(this, mAttentionModelListModels, mImageLoader, this);
	}

	private void initViews() {
		mEmptyView = ViewUtil.getEmptyView(this, getString(R.string.empty_info_my_attention_list));
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_attention_user);
		ListView gridView = mPullToRefreshListView.getRefreshableView();
		gridView.setCacheColorHint(getResources().getColor(R.color.transparent));
		gridView.setFadingEdgeLength(0);
		gridView.setOnItemClickListener(this);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh() {
				mCurrentPage = 0;
				getfansList(PULL_DOWN);
			}

			@Override
			public void onPullUpToRefresh() {
				getfansList(PULL_UP);
			}
		});
		mPullToRefreshListView.setIsShowHeaderFresh(true);
		mPullToRefreshListView.setHeaderVisible(true);
		mPullToRefreshListView.setIsShowHeaderFresh(true);
		mPullToRefreshListView.setMode(Mode.BOTH);
		gridView.setAdapter(mAttentionListAdapter);
	}

	protected void showData(ActionResult result, boolean isSuccess) {
		List<AttentionModel> models = (List<AttentionModel>) result.ResultObject;
		if (null != models) {
			mAttentionModelListModels.addAll(models);
		}
		if (mAttentionListAdapter != null) {
			mAttentionListAdapter.notifyDataSetChanged();
		}
	}

	private void getfansList(final int status) {
		if (mIsGetFansList) {
			return;
		}
		mIsGetFansList = true;
		mPullToRefreshListView.setVisibility(View.VISIBLE);
		new ActionProcessor().startAction(MyAttentionActivity.this, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				sendMessageToHandler(GET_FANS_LIST_SUCCESS, status, result);
			}

			@Override
			public void onError(ActionResult result) {

				sendMessageToHandler(GET_FANS_LIST_FAIL, status, result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return FansReq.getAttentionList(mCurrentPage);
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

			case R.id.attention_title_with_back_title_btn_left:
				finish();
				break;
			case R.id.ibtn_relation_fans:
				AttentionModel model = (AttentionModel) view.getTag();
				showPickPop(model.getUid());
				break;
			default:
				break;
		}
	}

	private void showPickPop(final String followuid) {
		final View popView = View.inflate(this, R.layout.view_actionsheet, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);
		final Button btn2 = (Button) popView.findViewById(R.id.btn_action_2);
		final TextView title = (TextView) popView.findViewById(R.id.tv_action_sheet_title);
		title.setText("选择操作");
		UIUtil.setViewGone(btn2);
		final Button btn1 = (Button) popView.findViewById(R.id.btn_action_1);
		btn1.setText("取消关注");
		btn1.setTextColor(Color.RED);
		final Button btnCancel = (Button) popView.findViewById(R.id.btn_cancel);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				follow("2", followuid);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
			}
		});
		popManager.show(Gravity.BOTTOM);
	}

	private void sendMessageToHandler(int what, int status, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.arg1 = status;
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	private void follow(final String type, final String followuid) {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				showErrorMsg(result);
				mPullToRefreshListView.setHeaderVisible(true);
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.userFollow(type, followuid);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AttentionModel model = (AttentionModel) parent.getAdapter().getItem(position);
		if (null == model || StringUtil.isNullOrEmpty(model.getUid())) {
			return;
		}
		Intent intent = new Intent(MyAttentionActivity.this, OtherHomeActivity.class);
		intent.putExtra("touid", model.getUid());
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}

}
