package com.xhr88.lp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.R;

public class EditNickNameActivity extends TraineeBaseActivity implements OnClickListener {

	private EditText mEdtNickName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_edit_nickname);
		setupView();
	}

	private void setupView() {
		mEdtNickName = (EditText) this.findViewById(R.id.edt_nick_name);
		String userName = getIntent().getStringExtra("username");
		if (!StringUtil.isNullOrEmpty(userName)) {
			mEdtNickName.setText(userName);
			mEdtNickName.setSelection(userName.length());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				String nickName = mEdtNickName.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(nickName)) {
					toast("昵称不能为空");
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("nickname", StringUtil.replaceBlank(nickName));
				setResult(RESULT_OK, intent);
				finish();
				break;
			case R.id.iv_delete:
				mEdtNickName.setText("");
				break;

			default:
				break;
		}
	}
}
