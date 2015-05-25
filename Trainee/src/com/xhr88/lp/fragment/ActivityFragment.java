package com.xhr88.lp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.common.ServerAPIConstant;

public class ActivityFragment extends FragmentBase implements OnClickListener {

	private WebView mWebView;
	private View mErrorView;
	private LoadingUpView mLoadingUpView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initVariables();
		super.onCreate(savedInstanceState);
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(getActivity(), true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View layout = inflater.inflate(R.layout.activity_activity, container, false);
		initViews(layout);
		configWebview();
		loadUrl();
		return layout;
	}

	private void initViews(View layout) {
		mWebView = (WebView) layout.findViewById(R.id.wv_update_content);
		mErrorView = layout.findViewById(R.id.ll_error_view);
		mErrorView.setOnClickListener(this);
	}

	private void loadUrl() {
		mWebView.loadUrl(ServerAPIConstant.getActivityUrl());
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void configWebview() {
		// 允许javascript代码执行
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setAppCacheEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setAllowFileAccess(true);
		settings.setDefaultTextEncodingName("utf-8");
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				showLoadingUpView(mLoadingUpView);
				EvtLog.d("aaa", "onPageStarted");
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView webView, String url) {
				dismissLoadingUpView(mLoadingUpView);
				EvtLog.d("aaa", "onPageFinished");
				super.onPageFinished(webView, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				dismissLoadingUpView(mLoadingUpView);
				UIUtil.setViewVisible(mErrorView);
				UIUtil.setViewGone(mWebView);
				EvtLog.d("aaa", "onReceivedError");
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView webView, String url) {
				Uri uri = Uri.parse(url);
				EvtLog.d("aaa", "shouldOverrideUrlLoading");
				if (uri.getScheme().equals("xhr") && uri.getHost().equals("com.xhr88.lp")) {
					startActivity(new Intent("android.intent.action.VIEW", uri));
					return true;
				} else {
					return super.shouldOverrideUrlLoading(webView, url);
				}
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			// 使webview可以更新进度条
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				getActivity().setProgress(newProgress * 100);
				if (newProgress == 100) {
					dismissLoadingUpView(mLoadingUpView);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_error_view:
				UIUtil.setViewVisible(mWebView);
				UIUtil.setViewGone(mErrorView);
				mWebView.reload();
				break;

			default:
				break;
		}

	}

}
