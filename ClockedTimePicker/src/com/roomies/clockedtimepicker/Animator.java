package com.roomies.clockedtimepicker;

import java.util.ArrayList;

import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class Animator {

	public static final int PLAY_SEQUENTIALLY = 1;
	public static final int PLAY_TOGETHER = 2;
	public static final int LINEAR_INTERPOLATOR = 0;
	public static final int ACCELERATOR_INTERPOLATOR = 1;
	public static final int DECELERATOR_INTERPOLATOR = 2;
	public static final int ACCELERATOR_DECELERATOR_INTERPOLATOR = 3;
	public static final int BOUNCE_INTERPOLATOR = 4;

	public static void animateObjects(int startOffset, int duration, int mode,
			int interpolator, AnimatorListener mListener,
			ObjectAnimator... values) {

		ArrayList<ObjectAnimator> arrayListObjectAnimators = new ArrayList<ObjectAnimator>();
		for (ObjectAnimator anim : values)
			arrayListObjectAnimators.add(anim);

		animateObjects(startOffset, duration, mode, interpolator, mListener,
				arrayListObjectAnimators);
	}

	public static void animateObjects(int startOffset, final int duration,
			final int mode, final int interpolator,
			final AnimatorListener mListener,
			final ArrayList<ObjectAnimator> anims) {

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				if (anims.size() > 0) {
					ObjectAnimator[] objectAnimators = anims
							.toArray(new ObjectAnimator[anims.size()]);
					AnimatorSet animSetXY = new AnimatorSet();
					animSetXY.setDuration(duration);
					switch (interpolator) {
					case LINEAR_INTERPOLATOR:
						animSetXY.setInterpolator(new LinearInterpolator());
						break;
					case ACCELERATOR_INTERPOLATOR:
						animSetXY.setInterpolator(new AccelerateInterpolator());
						break;
					case DECELERATOR_INTERPOLATOR:
						animSetXY.setInterpolator(new DecelerateInterpolator());
						break;
					case ACCELERATOR_DECELERATOR_INTERPOLATOR:
						animSetXY
								.setInterpolator(new AccelerateDecelerateInterpolator());
						break;
					case BOUNCE_INTERPOLATOR:
						animSetXY.setInterpolator(new BounceInterpolator());
						break;

					}
					if (mode == PLAY_SEQUENTIALLY) {
						animSetXY.playSequentially(objectAnimators);
						animSetXY.start();
					} else if (mode == PLAY_TOGETHER) {
						animSetXY.playTogether(objectAnimators);
						animSetXY.start();
					} else {

						animSetXY.start();
					}
					if (mListener != null)
						animSetXY.addListener(mListener);
				}
			}
		}, startOffset);
	}

}
