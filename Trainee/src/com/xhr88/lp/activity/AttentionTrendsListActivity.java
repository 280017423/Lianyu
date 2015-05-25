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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.xhr.framework.util.ImeUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshListView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.AttentionTrendsListAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.TrendReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.AttentionTrendsModel;
import com.xhr88.lp.model.datamodel.CommentModel;
import com.xhr88.lp.util.ViewUtil;
import com.xhr88.lp.widget.MListView;

/**
 * 我关注的动态列表
 * 
 * @author zou.sq
 * 
 */
public class AttentionTrendsListActivity extends TraineeBaseActivity implements OnClickListener, OnItemClickListener {

	private static final int REQUEST_CODE_ADD_TRENDS = 100;
	private static final int GET_TRENDS_LIST_FAIL = 0;
	private static final int GET_TRENDS_LIST_SUCCESS = 1;
	private static final int PULL_DOWN = 0;
	private static final int PULL_UP = 1;
	private List<AttentionTrendsModel> mAttentionTrendsModels;
	private PullToRefreshListView mPullToRefreshListView;
	private int mCurrentPage;
	private AttentionTrendsListAdapter mTrendsListAdapter;
	private boolean mIsGetTrends;
	private LoadingUpView mLoadingUpView;
	private View mViewInput;
	private EditText mEdtComment;
	private View mEmptyView;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case GET_TRENDS_LIST_SUCCESS:
					mPullToRefreshListView.onRefreshComplete();
					List<AttentionTrendsModel> models = (List<AttentionTrendsModel>) result.ResultObject;
					mCurrentPage++;
					mPullToRefreshListView.setMode(Mode.BOTH);
					if (PULL_DOWN == msg.arg1) {
						mAttentionTrendsModels.clear();
					}
					if (null != models && models.size() < ConstantSet.PAGE_SIZE) {
						mPullToRefreshListView.setNoMoreData();
					}
					showData(result, true);
					if (null != mAttentionTrendsModels && mAttentionTrendsModels.isEmpty()) {
						mPullToRefreshListView.setEmptyView(mEmptyView);
					}
					break;
				case GET_TRENDS_LIST_FAIL:
					if (mAttentionTrendsModels.isEmpty()) {
						// 当前列表无数据，网络异常并且本里无缓存数据，那么显示错误界面
						if (result.ResultCode != null && ActionResult.RESULT_CODE_NET_ERROR.equals(result.ResultCode)
								&& result.ResultObject != null
								&& !((List<AttentionTrendsModel>) result.ResultObject).isEmpty()) {
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
			mIsGetTrends = false;
		}
	};

	protected void showData(ActionResult result, boolean isSuccess) {
		List<AttentionTrendsModel> models = (List<AttentionTrendsModel>) result.ResultObject;
		if (null != models) {
			mAttentionTrendsModels.addAll(models);
		}
		if (mTrendsListAdapter != null) {
			mTrendsListAdapter.notifyDataSetChanged();
		}
	}

	private void initVariables() {
		mAttentionTrendsModels = new ArrayList<AttentionTrendsModel>();
		mTrendsListAdapter = new AttentionTrendsListAdapter(this, mAttentionTrendsModels, mImageLoader, this);
		mLoadingUpView = new LoadingUpView(this, true);
	}

	private void initViews() {
		mEmptyView = ViewUtil.getEmptyView(this, getString(R.string.empty_info_attention_trends_list));
		mViewInput = findViewById(R.id.rl_input_layout);
		mEdtComment = (EditText) findViewById(R.id.edt_add_comment);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_trends_list);
		ListView listView = mPullToRefreshListView.getRefreshableView();
		listView.setCacheColorHint(getResources().getColor(R.color.transparent));
		listView.setFadingEdgeLength(0);
		listView.setOnItemClickListener(this);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh() {
				mCurrentPage = 0;
				getTrendsList(PULL_DOWN);
			}

			@Override
			public void onPullUpToRefresh() {
				getTrendsList(PULL_UP);
			}
		});
		mPullToRefreshListView.setIsShowHeaderFresh(true);
		mPullToRefreshListView.setHeaderVisible(true);
		mPullToRefreshListView.setIsShowHeaderFresh(true);
		mPullToRefreshListView.setMode(Mode.BOTH);
		listView.setAdapter(mTrendsListAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attention_trends_list);
		initVariables();
		initViews();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AttentionTrendsModel model = (AttentionTrendsModel) parent.getAdapter().getItem(position);
		if (null == model || StringUtil.isNullOrEmpty(model.getTid())) {
			return;
		}
		Intent intent = new Intent(AttentionTrendsListActivity.this, TrendsDetailActivity.class);
		intent.putExtra(ConstantSet.KEY_TID, model.getTid());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.trend_with_back_title_btn_left:
				finish();
				break;
			case R.id.btn_dynamic_like:
				AttentionTrendsModel model = (AttentionTrendsModel) v.getTag();
				if (null != model && null != model.getTid() && 1 != model.getIsgood()) {
					trendsLike(model);
				}
				break;
			case R.id.ibtn_head_img:
				AttentionTrendsModel dynamicModel1 = (AttentionTrendsModel) v.getTag();
				if (null != dynamicModel1 && !StringUtil.isNullOrEmpty(dynamicModel1.getUid())) {
					Intent intent1 = new Intent(AttentionTrendsListActivity.this, OtherHomeActivity.class);
					intent1.putExtra("touid", dynamicModel1.getUid());
					startActivity(intent1);
				}
				break;
			case R.id.btn_dynamic_comment:
				AttentionTrendsModel dynamicModel = (AttentionTrendsModel) v.getTag(R.id.tag1);
				if (null != dynamicModel && null != dynamicModel.getTid()) {
					mViewInput.setVisibility(View.VISIBLE);
					mEdtComment.setTag(R.id.tag1, dynamicModel);
					mEdtComment.setTag(R.id.tag2, v.getTag(R.id.tag2));
					mEdtComment.requestFocus();
					ImeUtil.showSoftInput(this);
				}
				break;
			case R.id.btn_send_comment:
				AttentionTrendsModel commentModel = (AttentionTrendsModel) mEdtComment.getTag(R.id.tag1);
				MListView mListView = (MListView) mEdtComment.getTag(R.id.tag2);

				BaseAdapter adapter = ((BaseAdapter) mListView.getAdapter());
				String comment = mEdtComment.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(comment)) {
					toast("评论不能为空");
					return;
				}
				if (null != commentModel && null != commentModel.getTid()) {
					addComment(commentModel, adapter, comment);
				}
				break;

			default:
				break;
		}
	}

	private void addComment(final AttentionTrendsModel commentModel, final BaseAdapter adapter, final String content) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				toast("评论成功");
				ImeUtil.hideSoftInput(AttentionTrendsListActivity.this, mEdtComment);
				CommentModel model = (CommentModel) result.ResultObject;
				commentModel.getCommentlist().add(model);
				adapter.notifyDataSetChanged();
				mEdtComment.setText("");
				mViewInput.setVisibility(View.GONE);
				// mPullToRefreshListView.setHeaderVisible(true);
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.commentAdd(commentModel.getTid(), content, "");
			}
		});
	}

	private void trendsLike(final AttentionTrendsModel model) {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				toast("喜欢成功");
				for (int i = 0; i < mAttentionTrendsModels.size(); i++) {
					AttentionTrendsModel tempModel = mAttentionTrendsModels.get(i);
					if (model.getTid().equals(tempModel.getTid())) {
						tempModel.setIsgood(1);
						mTrendsListAdapter.notifyDataSetChanged();
						return;
					}
				}
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.trendsLike(model.getTid(), "1");
			}
		});
	}

	private void getTrendsList(final int status) {
		if (mIsGetTrends) {
			return;
		}
		mIsGetTrends = true;
		UIUtil.setViewVisible(mPullToRefreshListView);
		new ActionProcessor().startAction(this, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				sendMessageToHandler(GET_TRENDS_LIST_SUCCESS, status, result);
			}

			@Override
			public void onError(ActionResult result) {
				sendMessageToHandler(GET_TRENDS_LIST_FAIL, status, result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.getAttentionTrendsList(mCurrentPage);

			}
		});

	}

	private void sendMessageToHandler(int what, int status, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.arg1 = status;
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_ADD_TRENDS) {
			mPullToRefreshListView.setHeaderVisible(true);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		if (View.VISIBLE == mViewInput.getVisibility()) {
			UIUtil.setViewGone(mViewInput);
			return;
		} else {
			finish();
		}
		super.onBackPressed();
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}

}
