package com.roomies.clockedtimepicker;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class TimeTextView extends TextView {

	private static String TAG;
	private static final int EXTRA_PADDING = 25;

	public interface OnTimeClickedListener {
		public void onHoursClicked();

		public void onMinutesClicked();

		public void onDenominatorClicked();
	}

	private OnTimeClickedListener mTimeClickedListener;
	private Time mTime;
	private float hourTextLength;
	private float minuteTextLength;
	private float denominatorTextLength;

	public TimeTextView(Context context) {
		super(context);
		init(context, null);
	}

	public TimeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TAG = getClass().getSimpleName() + "-> ";
		setClickable(true);
	}

	public void setOnTimeClickedListener(
			OnTimeClickedListener mTimeClickedListener) {
		this.mTimeClickedListener = mTimeClickedListener;
	}

	public Time getTime() {
		return mTime;
	}

	public void setTime(Time time) {
		this.mTime = time;

		Spannable textSpan = new SpannableString(time.getHour() + ":"
				+ time.getMinutes() + " " + time.getDenominator());
		hourTextLength = getPaint().measureText("" + time.getHour());
		minuteTextLength = getPaint().measureText("" + time.getMinutes());
		denominatorTextLength = getPaint().measureText(
				"" + time.getDenominator());

		int denominatorOffset = ("" + time.getHour()).length() + ":".length()
				+ ("" + time.getMinutes()).length() + " ".length();
		textSpan.setSpan(new RelativeSizeSpan(0.50f), denominatorOffset,
				textSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		setText(textSpan);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			final int x = (int) event.getX();

			if (x > (getMeasuredWidth() / 2 - hourTextLength - EXTRA_PADDING)
					&& x < getMeasuredWidth() / 2) {
				Logger.Log(TAG + "Hour clicked");
				if (mTimeClickedListener != null)
					mTimeClickedListener.onHoursClicked();
			} else if (x > getMeasuredWidth() / 2
					&& x < (getMeasuredWidth() / 2 + minuteTextLength)) {
				Logger.Log(TAG + "minute clicked");
				if (mTimeClickedListener != null)
					mTimeClickedListener.onMinutesClicked();
			} else if (x > (getMeasuredWidth() / 2 + minuteTextLength)
					&& x < (getMeasuredWidth() / 2 + minuteTextLength + denominatorTextLength)) {
				Logger.Log(TAG + "denominator clicked");
				if (mTimeClickedListener != null)
					mTimeClickedListener.onDenominatorClicked();
			}
		}
		return true;
	}

	public String switchDenominator() {
		if (mTime != null) {
			Logger.Log(TAG + "Time: " + mTime);
			if (mTime.getDenominator().contentEquals("AM")) {
				mTime.setDenominator("PM");
				setTime(mTime);
				return "PM";
			}

			mTime.setDenominator("AM");
			setTime(mTime);
		}
		return "AM";
	}

}
