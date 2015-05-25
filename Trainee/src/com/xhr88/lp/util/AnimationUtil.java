package com.xhr88.lp.util;

import com.xhr88.lp.R;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

public class AnimationUtil {

	public static void startRotaeAnimation(View view) {

		Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.view_rotae);
		animation.setInterpolator(new LinearInterpolator());// 匀速
		view.setAnimation(animation);
		view.startAnimation(animation);
	}
}
