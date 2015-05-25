package com.xhr88.lp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.PayModel;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.PayResult;

/**
 * 在线支付界面
 * 
 * @author zou.sq
 */
public class OnlinePayActivity extends TraineeBaseActivity implements OnClickListener {

	private static final int SDK_PAY_FLAG = 1;

	private TextView mTvCoin;
	private TextView mTvMoney;
	private int mBuyCoin;
	private PayModel mPayModel;
	private LoadingUpView mLoadingUpView;

	private Handler mPayHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					PayResult payResult = new PayResult((String) msg.obj);
					// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
					String resultInfo = payResult.getResult();
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						if (UserMgr.hasUserInfo()) {
							UserViewModel userInfo = UserDao.getLocalUserInfo();
							int currentCoin = userInfo.UserInfo.getCoin() + mBuyCoin;
							userInfo.UserInfo.setCoin(currentCoin);
							UserDao.saveLocalUserInfo(userInfo);
						}
						toast("支付成功");
						finish();
					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							toast("支付结果确认中");
						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							toast("支付失败");
						}
					}
					break;
				}
				default:
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_pay);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this, true);
		mPayModel = (PayModel) getIntent().getSerializableExtra(PayModel.class.getName());
		if (null == mPayModel) {
			finish();
		}
		mBuyCoin = mPayModel.getCoin() + mPayModel.getGive();
	}

	private void initViews() {
		mTvCoin = (TextView) findViewById(R.id.tv_coin);
		mTvMoney = (TextView) findViewById(R.id.tv_money);
		mTvCoin.setText(mPayModel.getCoin() + ",");
		mTvMoney.setText("￥" + mPayModel.getMoney() + "元");
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.btn_pay:
				charge("" + mPayModel.getMoney());
				break;

			default:
				break;
		}
	}

	private void charge(final String money) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(OnlinePayActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (null == result) {
					return;
				}
				String orderInfo = (String) result.ResultObject;
				if (!StringUtil.isNullOrEmpty(orderInfo)) {
					pay(orderInfo);
				}
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.charge("1", money);
			}
		});
	}

	private void pay(final String payInfo) {
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(OnlinePayActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);
				Message msg = mPayHandler.obtainMessage(SDK_PAY_FLAG, result);
				mPayHandler.sendMessage(msg);
			}
		};
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

}
