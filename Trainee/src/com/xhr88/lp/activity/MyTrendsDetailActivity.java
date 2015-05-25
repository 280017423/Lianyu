package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.ImeUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr.framework.widget.pulltorefresh.XScrollView;
import com.xhr.framework.widget.pulltorefresh.XScrollView.IXScrollViewListener;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.MainActivity.TabHomeIndex;
import com.xhr88.lp.adapter.CommentListAdapter;
import com.xhr88.lp.adapter.TrendsImageDetailAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.TrendReq;
import com.xhr88.lp.business.request.TrendsReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.CommentModel;
import com.xhr88.lp.model.datamodel.TrendsInfoModel;
import com.xhr88.lp.util.DateUtil;
import com.xhr88.lp.util.PopWindowUtil;
import com.xhr88.lp.widget.AutoScrollViewPager;
import com.xhr88.lp.widget.CircleFlowIndicator;
import com.xhr88.lp.widget.MListView;

/**
 * 我的动态详情界面
 * 
 * @author zou.sq
 */
public class MyTrendsDetailActivity extends TraineeBaseActivity implements OnClickListener {

	private static final int DIALOG_DELETE_TRENDS = 2;

	private String mTid;
	private int mCurrentPage;
	private RoundCornerImageView mIvHead;
	private View mVGallery;
	private View mViewTab;
	private View mViewInput;
	private View mViewChat;
	private View mViewDelete;
	private View mViewDeleteLine;
	private View mViewComment;
	private TextView mTvNickname;
	private TextView mTvCommentNum;
	private ImageView mIvSex;
	private TextView mTvAge;
	private TextView mTvConstellatory;
	private TextView mTvTime;
	private TextView mTvContent;
	private TextView mTvIsGood;
	private ImageView mIvHasVideo;
	private ImageView mIvLevel;
	private ImageView mIvEmpty;
	private AutoScrollViewPager mViewPager;
	private EditText mEdtComment;
	private CircleFlowIndicator mCircleFlowIndicator;
	private LoadingUpView mLoadingUpView;
	private ArrayList<String> mHeadimgList;
	private ArrayList<String> mBigHeadimgList;
	private TrendsImageDetailAdapter mTrendsViewPageAdapter;
	private List<CommentModel> mCommentModelListModels;
	private CommentListAdapter mCommentListAdapter;
	private MListView mLvComment;
	private XScrollView mSvNomal;
	private ImageButton mIbtnRelation;
	private int mWidth;
	private TrendsInfoModel mCurrentInfoModel;
	private int mShareFeedCount;
	private boolean mIsLikeSuccess; // 缺陷 #834

	protected void showData(ActionResult result) {
		mCurrentInfoModel = (TrendsInfoModel) result.ResultObject;
		if (mCurrentInfoModel == null) {
			return;
		}
		mTvContent.setText(mCurrentInfoModel.getContent());
		mTvNickname.setText(mCurrentInfoModel.getNickname());
		try {
			mTvAge.setText(mCurrentInfoModel.getAge());
		} catch (Exception e) {
			e.printStackTrace();
		}
		mIvLevel.setImageResource(ConstantSet.getLevelIcons(mCurrentInfoModel.getLevel()));
		mTvConstellatory.setText(mCurrentInfoModel.getConstellatory());
		if (1 == mCurrentInfoModel.getIsvideo()) {
			UIUtil.setViewVisible(mIvHasVideo);
		} else {
			UIUtil.setViewGone(mIvHasVideo);
		}
		int commentNum = mCurrentInfoModel.getCommentnum();
		mTvCommentNum.setText("评论（" + commentNum + "）");

		int isGood = mCurrentInfoModel.getIsgood();
		if (1 == isGood) {
			mTvIsGood.setText(mCurrentInfoModel.getGoodnum() + "");
			mTvIsGood.setCompoundDrawables(null, getArrowDrawable(R.drawable.dynamic_liked), null, null);
		} else {
			mTvIsGood.setText("喜欢");
			mTvIsGood.setCompoundDrawables(null, getArrowDrawable(R.drawable.dynamic_item_like), null, null);

		}
		int sex = mCurrentInfoModel.getSex();
		int imageRes = R.drawable.default_man_head_bg;
		if (1 == sex) {
			mIvSex.setImageResource(R.drawable.icon_man);
		} else {
			imageRes = R.drawable.default_woman_head_bg;
			mIvSex.setImageResource(R.drawable.icon_woman);
		}
		String headUrl = mCurrentInfoModel.getHeadimg();
		mImageLoader.displayImage(headUrl, mIvHead, new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageRes).build());
		String mHead = mCurrentInfoModel.getResource();
		if (StringUtil.isNullOrEmpty(mHead)) {
			mVGallery.setVisibility(View.GONE);
		} else {
			mVGallery.setVisibility(View.VISIBLE);
			List<String> sourceList = Arrays.asList(mHead.split(","));
			List<String> bigSourceList = Arrays.asList(mCurrentInfoModel.getBigresource().split(","));
			if (sourceList != null && sourceList.size() > 0) {
				mHeadimgList.clear();
				mHeadimgList.addAll(sourceList);
				mBigHeadimgList.clear();
				mBigHeadimgList.addAll(bigSourceList);
				mTrendsViewPageAdapter.notifyDataSetChanged();
				if (sourceList.size() <= 1) {
					mCircleFlowIndicator.setVisibility(View.GONE);
				} else {
					mCircleFlowIndicator.setVisibility(View.VISIBLE);
					mCircleFlowIndicator.setCount(sourceList.size());
					mCircleFlowIndicator.setSeletion(0);
				}
			}
		}
		try {
			mTvTime.setText(DateUtil.getCommentTime(mCurrentInfoModel.getCreatetime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Drawable getArrowDrawable(int id) {
		Drawable drawable = getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trend_detail);
		initVariables();
		initViews();
		getTrendsInfo();
	}

	private void initVariables() {
		mWidth = UIUtil.getScreenWidth(this);
		mTid = getIntent().getStringExtra(ConstantSet.KEY_TID);
		if (StringUtil.isNullOrEmpty(mTid)) {
			finish();
		}
		mHeadimgList = new ArrayList<String>();
		mBigHeadimgList = new ArrayList<String>();
		mCommentModelListModels = new ArrayList<CommentModel>();
		mCommentListAdapter = new CommentListAdapter(
				this, mCommentModelListModels, mImageLoader, new OnClickListener() {

					@Override
					public void onClick(View v) {
						String uid = (String) v.getTag();
						if (UserMgr.isMineUid(uid)) {
							MainActivity.INSTANCE.setTabSelection(TabHomeIndex.HOME_MY_CENTER, false);
							finish();
						} else {
							Intent intent = new Intent(MyTrendsDetailActivity.this, OtherHomeActivity.class);
							intent.putExtra("touid", uid);
							startActivity(intent);
						}
					}
				});
		mTrendsViewPageAdapter = new TrendsImageDetailAdapter(this, mHeadimgList, mBigHeadimgList, mImageLoader);
		mTrendsViewPageAdapter.hideLoading(true);
		mLoadingUpView = new LoadingUpView(this, true);
	}

	private void initViews() {
		initScrollView();
		View header = View.inflate(this, R.layout.activity_trends_detail_header, null);
		mIvHead = (RoundCornerImageView) header.findViewById(R.id.iv_head_img);
		mVGallery = header.findViewById(R.id.rl_gallery_layout);
		mLvComment = (MListView) header.findViewById(R.id.my_comment_list);
		mIvEmpty = (ImageView) header.findViewById(R.id.iv_empty_view);
		mViewComment = header.findViewById(R.id.ll_comment);
		mLvComment.setAdapter(mCommentListAdapter);
		UIUtil.setViewWidth(mIvHead, mWidth / 5);
		UIUtil.setViewHeight(mIvHead, mWidth / 5);
		UIUtil.setViewWidth(mVGallery, mWidth);
		UIUtil.setViewHeight(mVGallery, mWidth);
		mIbtnRelation = (ImageButton) header.findViewById(R.id.ibtn_relation);
		UIUtil.setViewGone(mIbtnRelation);
		mTvNickname = (TextView) header.findViewById(R.id.tv_nickname);
		mTvCommentNum = (TextView) header.findViewById(R.id.tv_comment_num);
		mTvAge = (TextView) header.findViewById(R.id.tv_age);
		mTvConstellatory = (TextView) header.findViewById(R.id.tv_constellatory);
		mIvHasVideo = (ImageView) header.findViewById(R.id.iv_isvideo);
		mViewDelete = header.findViewById(R.id.btn_delete_trends);
		mViewDeleteLine = header.findViewById(R.id.btn_delete_trends_line);
		UIUtil.setViewVisible(mViewDelete);
		UIUtil.setViewVisible(mViewDeleteLine);
		mTvTime = (TextView) header.findViewById(R.id.tv_trends_info_time);
		mTvContent = (TextView) header.findViewById(R.id.tv_trend_cotent);
		mTvIsGood = (TextView) findViewById(R.id.tv_good);
		mIvSex = (ImageView) header.findViewById(R.id.iv_sex);
		mViewPager = (AutoScrollViewPager) header.findViewById(R.id.vp_recommand_vedio);
		mViewPager.setAdapter(mTrendsViewPageAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mCircleFlowIndicator.setSeletion(position % mHeadimgList.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		mCircleFlowIndicator = (CircleFlowIndicator) header.findViewById(R.id.cfi_indicator);
		mIvLevel = (ImageView) header.findViewById(R.id.iv_level);
		mSvNomal.setView(header);

		mViewTab = findViewById(R.id.ll_buttom_tab);
		mViewInput = findViewById(R.id.rl_input_layout);
		mViewChat = findViewById(R.id.rl_trend_detail_chat);
		UIUtil.setViewGone(mViewChat);
		mEdtComment = (EditText) findViewById(R.id.edt_add_comment);
		mLvComment.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CommentModel model = (CommentModel) parent.getAdapter().getItem(position);
				if (null == model) {
					return;
				}
				if (UserMgr.isMineUid(model.getUid())) {
					if (!StringUtil.isNullOrEmpty(model.getCid())) {
						showDelPop(model.getCid());
					}
				} else {
					mEdtComment.setTag(model.getUid());
					mEdtComment.setText("");
					mEdtComment.setHint("回复" + model.getNickname() + "：");
					mEdtComment.requestFocus();
					UIUtil.setViewGone(mViewTab);
					UIUtil.setViewVisible(mViewInput);
					ImeUtil.showSoftInput(MyTrendsDetailActivity.this);
				}

			}
		});
	}

	private void initScrollView() {
		mSvNomal = (XScrollView) findViewById(R.id.sl_normal_view);
		mSvNomal.setHeaderDivider(getResources().getColor(R.color.transparent));
		mSvNomal.setPullRefreshEnable(false);
		mSvNomal.setPullLoadEnable(true);
		mSvNomal.setAutoLoadEnable(false);
		mSvNomal.setIXScrollViewListener(new IXScrollViewListener() {

			@Override
			public void onRefresh() {
				// 不做任何事情
			}

			@Override
			public void onLoadMore() {
				getCommentLists(false, false);
			}
		});
	}

	private void getTrendsInfo() {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				showData(result);
				getCommentLists(false, true);// 获取成功之后才获取评论列表
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendsReq.getTrendInfo(mTid);
			}
		});
	}

	private void getCommentLists(boolean showLoading, final boolean isAuto) {
		if (showLoading) {
			showLoadingUpView(mLoadingUpView);
		}
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (result == null) {
					return;
				}
				List<CommentModel> commentModels = (List<CommentModel>) result.ResultObject;
				if (null == commentModels) {
					return;
				}
				if (0 == mCurrentPage) {
					mCommentModelListModels.clear();
				}
				mCommentModelListModels.addAll(commentModels);
				mCommentListAdapter.notifyDataSetChanged();
				mCurrentPage++;
				if (commentModels.size() < ConstantSet.COMMENTS_PAGE_SIZE) {
					mSvNomal.setNoMoreData(false);
				} else {
					mSvNomal.setNoMoreData(true);
				}
				if (isAuto) {
					onFinishLoad();
				} else {
					mSvNomal.stopRefresh();
					mSvNomal.stopLoadMore();
					mLvComment.setSelection(mCommentModelListModels.size());
				}
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
				onFinishLoad();
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendsReq.getCommentLists(mTid, mCurrentPage);
			}
		});
	}

	private void onFinishLoad() {
		mSvNomal.stopRefresh();
		mSvNomal.stopLoadMore();
		int millisInFuture = 0;
		if (mHeadimgList.isEmpty()) {
			millisInFuture = 10;
		}
		new CountDownTimer(millisInFuture, millisInFuture) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				mSvNomal.scrollTo(0, 0);
				if (UIUtil.getScreenHeight(MyTrendsDetailActivity.this) > mViewComment.getBottom()
						+ UIUtil.dip2px(MyTrendsDetailActivity.this, 45 * 2)) {
					// 重新计算高度，要是没有填充一屏的就填充满屏幕
					UIUtil.setViewHeight(
							mIvEmpty,
							UIUtil.getScreenHeight(MyTrendsDetailActivity.this) - mViewComment.getBottom()
									- UIUtil.dip2px(MyTrendsDetailActivity.this, 45 * 2));
				}
			}
		}.start();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				if (mIsLikeSuccess) {
					setResult(RESULT_OK);
				}
				finish();
				break;
			case R.id.iv_head_img:
				MainActivity.INSTANCE.setTabSelection(TabHomeIndex.HOME_MY_CENTER, false);
				finish();
				break;
			case R.id.ibtn_share:
				if (null == mCurrentInfoModel) {
					return;
				}
				showShare(mCurrentInfoModel.getContent(), mCurrentInfoModel.getShareurl());
				break;
			case R.id.rl_trend_detail_like:
				if (null == mCurrentInfoModel || 1 == mCurrentInfoModel.getIsgood()) {
					return;
				}
				trendsLike();
				break;
			case R.id.btn_delete_trends:
				if (null == mCurrentInfoModel) {
					return;
				}
				showDialog(DIALOG_DELETE_TRENDS);
				break;
			case R.id.btn_send_comment:
				if (null == mCurrentInfoModel) {
					return;
				}
				String comment = mEdtComment.getText().toString().trim();
				Object communid = mEdtComment.getTag();
				if (StringUtil.isNullOrEmpty(comment)) {
					toast("评论不能为空");
					return;
				}
				if (null == communid) {
					addComment(mTid, comment, "");
				} else {
					addComment(mTid, comment, (String) communid);
				}
				break;
			case R.id.rl_trend_detail_comment:
				if (null == mCurrentInfoModel) {
					return;
				}
				mEdtComment.setText("");
				mEdtComment.setTag("");
				mEdtComment.setHint("");
				mEdtComment.requestFocus();
				UIUtil.setViewGone(mViewTab);
				UIUtil.setViewVisible(mViewInput);
				ImeUtil.showSoftInput(this);
				break;
			default:
				break;
		}
	}

	private void addComment(final String tid, final String content, final String comuid) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				int commentNum = mCurrentInfoModel.getCommentnum();
				mCurrentInfoModel.setCommentnum(commentNum + 1);
				mTvCommentNum.setText("评论（" + (commentNum + 1) + "）");
				toast("评论成功");
				ImeUtil.hideSoftInput(MyTrendsDetailActivity.this, mEdtComment);
				UIUtil.setViewGone(mViewInput);
				UIUtil.setViewVisible(mViewTab);
				mCurrentPage = 0; // 不然就是1
				getCommentLists(true, false);
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.commentAdd(tid, content, comuid);
			}
		});
	}

	private void trendsDelete() {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				toast("删除成功");
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.trendsDel(mTid);
			}
		});
	}

	private void trendsLike() {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				toast("喜欢成功");
				int isGood = mCurrentInfoModel.getIsgood();
				if (0 == isGood) {
					mCurrentInfoModel.setIsgood(1);
					mCurrentInfoModel.setGoodnum(mCurrentInfoModel.getGoodnum() + 1); // 手动加1
					mTvIsGood.setText(mCurrentInfoModel.getGoodnum() + "");
					mTvIsGood.setCompoundDrawables(null, getArrowDrawable(R.drawable.dynamic_liked), null, null);
					mIsLikeSuccess = true;
				}
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.trendsLike(mTid, "1");
			}
		});
	}

	private void commentDelete(final String cid) {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				int commentNum = mCurrentInfoModel.getCommentnum();
				mCurrentInfoModel.setCommentnum(commentNum - 1);
				mTvCommentNum.setText("评论（" + (commentNum - 1) + "）");
				for (int i = 0; i < mCommentModelListModels.size(); i++) {
					CommentModel model = mCommentModelListModels.get(i);
					if (cid.equals(model.getCid())) {
						mCommentModelListModels.remove(i);
						mCommentListAdapter.notifyDataSetChanged();
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
				return TrendReq.commentDel(cid);
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (View.VISIBLE == mViewInput.getVisibility()) {
			UIUtil.setViewGone(mViewInput);
			UIUtil.setViewVisible(mViewTab);
			return;
		}
		if (mIsLikeSuccess) {
			setResult(RESULT_OK);
		}
		finish();
		super.onBackPressed();
	}

	private void showDelPop(final String cid) {
		final View popView = View.inflate(this, R.layout.view_actionsheet, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);
		final Button btn2 = (Button) popView.findViewById(R.id.btn_action_2);
		final TextView title = (TextView) popView.findViewById(R.id.tv_action_sheet_title);
		title.setText("选择操作");
		UIUtil.setViewGone(btn2);
		final Button btn1 = (Button) popView.findViewById(R.id.btn_action_1);
		btn1.setText("删除");
		btn1.setTextColor(Color.RED);
		final Button btnCancel = (Button) popView.findViewById(R.id.btn_cancel);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				commentDelete(cid);
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

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_DELETE_TRENDS:
				return createDialogBuilder(this, getString(R.string.button_text_tips), "确定删除这条动态？",
						getString(R.string.button_text_no), "确定").create(id);
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_DELETE_TRENDS:
				trendsDelete();
				break;
			default:
				break;
		}
	}

	private void showShare(String content, String webUrl) {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		oks.disableSSOWhenAuthorize(); // 关闭sso授权
		oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name)); // 分享时Notification的图标和文字
		oks.setTitle(getString(R.string.app_name)); // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitleUrl(webUrl); // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setText(content); // text是分享文本，所有平台都需要这个字段
		if (View.VISIBLE == mVGallery.getVisibility()) {
			oks.setViewToShare(mVGallery);
		} else {
			oks.setViewToShare(mIvHead);
		}
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		oks.setUrl(webUrl); // url仅在微信（包括好友和朋友圈）中使用
		oks.setSite("动态详情"); // site是分享此内容的网站名称，仅在QQ空间使用

		oks.setSiteUrl(webUrl); // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		oks.setSilent(false); // 启动分享GUI
		oks.setShareFromQQAuthSupport(false);
		oks.setDialogMode(); // 令编辑页面显示为Dialog模式
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int action, Throwable trowable) {
			}

			@Override
			public void onComplete(Platform platform, int action, HashMap<String, Object> map) {
				// 成功之后调用回调 分享的平台id，1=微信好友，2=QQ, 3=新浪，4=邮件，信息，5=QQ空间，6=微信朋友圈
				int platformid = 1;
				if (Wechat.NAME.equals(platform.getName())) {
					platformid = 1;
				} else if (QQ.NAME.equals(platform.getName())) {
					platformid = 2;
				} else if (SinaWeibo.NAME.equals(platform.getName())) {
					platformid = 3;
				} else if (QZone.NAME.equals(platform.getName())) {
					platformid = 5;
				} else if (WechatMoments.NAME.equals(platform.getName())) {
					platformid = 6;
				} else {
					platformid = 4;
				}
				trendsShare(platformid);
			}

			@Override
			public void onCancel(Platform platform, int action) {
			}
		});
		oks.show(this);
	}

	private void trendsShare(final int platformid) {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				mShareFeedCount = 0;
			}

			@Override
			public void onError(ActionResult result) {
				mShareFeedCount++;
				if (3 > mShareFeedCount) {
					trendsShare(platformid);
				} else {
					mShareFeedCount = 0;
				}
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendsReq.share(mTid, platformid + "");
			}
		});
	}

}
