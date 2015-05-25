package com.xhr88.lp.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.LittleShopReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;

/**
 * 添加店铺简介界面
 * 
 * @author zou.sq
 */
public class AddShopIntroduceActivity extends TraineeBaseActivity implements OnClickListener {

	private static final int EDIT_LITTLE_SHOP = 100;
	private EditText mEdtIntroduce;
	private String mOldIntroduce;
	private LoadingUpView mLoadingUpView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_shop_introduce);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mOldIntroduce = getIntent().getStringExtra(ConstantSet.KEY_INTRODUCE_CONTENT);
		mLoadingUpView = new LoadingUpView(this);
	}

	private void initViews() {
		mEdtIntroduce = (EditText) findViewById(R.id.edt_shop_introduce);
		if (!StringUtil.isNullOrEmpty(mOldIntroduce)) {
			mEdtIntroduce.setText(mOldIntroduce);
			mEdtIntroduce.setSelection(mOldIntroduce.length());
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				back();
				break;
			case R.id.title_with_back_title_btn_right:
				checkIntroduce();
				break;

			default:
				break;
		}
	}

	private void checkIntroduce() {
		final String newDescription = mEdtIntroduce.getText().toString().trim();
		if (StringUtil.isNullOrEmpty(newDescription) || newDescription.length() < 15) {
			toast("至少15个字以上");
			return;
		}
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(AddShopIntroduceActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				Intent intent = new Intent(AddShopIntroduceActivity.this, MyLittleShopActivity.class);
				intent.putExtra(ConstantSet.KEY_INTRODUCE_CONTENT, newDescription);
				setResult(RESULT_OK, intent);
				finish();
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
				dismissLoadingUpView(mLoadingUpView);
			}

			@Override
			public ActionResult onAsyncRun() {
				return LittleShopReq.editShopInfo(newDescription, "", "", "");
			}
		});

	}

	private void back() {
		String newIntroduce = mEdtIntroduce.getText().toString().trim();
		if (null != newIntroduce && newIntroduce.equals(mOldIntroduce)) {
			finish();
			return;
		}
		showDialog(EDIT_LITTLE_SHOP);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if (EDIT_LITTLE_SHOP == id) {
			return createDialogBuilder(this, null, "您的小店已经修改，是否需要保存？", "保存", "不保存").create(id);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onPositiveBtnClick(int id, DialogInterface dialog, int which) {
		checkIntroduce();
		super.onPositiveBtnClick(id, dialog, which);
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		finish();
		super.onNegativeBtnClick(id, dialog, which);
	}

	@Override
	public void onBackPressed() {
		back();
	}
}
