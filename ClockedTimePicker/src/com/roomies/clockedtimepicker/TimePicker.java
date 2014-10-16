package com.roomies.clockedtimepicker;
/**
 * @author Purushottam Pawar
 */
import java.util.ArrayList;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Custom implementation of Time Picker widget which allows user to select the hours and
 * minutes from a circular dial view. The hours and minutes are placed in analog clock fashion.
 * Inspired from Google Keep reminder Time Picker Widget. 
 **/
public class TimePicker extends LinearLayout implements OnClickListener {

	private static final int MINIMUM_SIZE = 250;
	private static final int TOTAL_NICKS = 12;
	private static final float DEGREES_PER_NICK = 360.0f / TOTAL_NICKS;
	private static final int START_ANGLE = 300;
	private static final int ANGLE_ADJUSTMENT = 15;
	private static final int BACKGROUND_PADDING = 10;
	private static final int RIM_PADDING = 10;
	private static final int HANDLE_CIRCLE_RADIUS = 5;
	private static final int SELECTION_CIRCLE_RADIUS = 35;
	private static final int TOUCH_DELEGATE = 15;

	public interface OnTimeSelectedListener {
		public void onHourSelected(int hour);

		public void onMinutesSelected(int minutes);
	}

	private OnTimeSelectedListener mTimeSelectedListener;
	private Paint rimCirclePaint;
	private RectF rimRect;
	private RectF backRect;
	private Paint backRectPaint;
	private RectF centerRect;
	private Paint selectionPaint;
	private RectF selectionRect;
	private int childIndex;
	private int selectedViewIndex = -1;
	private boolean isSelectingMinutes;
	private int hours;
	private int minutes;

	public TimePicker(Context context) {
		super(context);

		init(context, null);
	}

	public TimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public int getSelectedHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int getSelectedMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	private void init(Context context, AttributeSet attrs) {
		setWillNotDraw(false);
		// initialize the drawing stuff here
		rimCirclePaint = new Paint();
		rimCirclePaint.setAntiAlias(true);
		rimCirclePaint.setStyle(Paint.Style.FILL);
		rimCirclePaint.setColor(Color.WHITE);

		backRectPaint = new Paint();
		backRectPaint.setAntiAlias(true);
		backRectPaint.setStyle(Paint.Style.FILL);
		backRectPaint.setColor(Color.parseColor("#DEDEDE"));

		selectionPaint = new Paint();
		selectionPaint.setAntiAlias(true);
		selectionPaint.setStyle(Paint.Style.FILL);
		selectionPaint.setStrokeWidth(5);
		selectionPaint.setColor(Color.parseColor("#aa43B1E8"));

		rimRect = new RectF();
		backRect = new RectF();
		centerRect = new RectF();
		selectionRect = new RectF();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawRect(backRect, backRectPaint);
		canvas.drawOval(rimRect, rimCirclePaint);
		canvas.drawOval(centerRect, backRectPaint);

		canvas.drawOval(selectionRect, selectionPaint);
	}

	public void setOnTimeSelectedListener(OnTimeSelectedListener mListener) {
		mTimeSelectedListener = mListener;
	}

	public void addHourViews(final Context context) {

		for (int i = 0; i < TOTAL_NICKS; i++) {
			final ExtraTouchableTextView hourTextView = new ExtraTouchableTextView(
					getContext());
			hourTextView.setTextColor(0xff555555);
			hourTextView.setTypeface(hourTextView.getTypeface(), Typeface.BOLD);
			hourTextView.setText("" + (i + 1));
			HourMinuteVo vo = new HourMinuteVo();
			vo.text = "" + (i + 1);
			hourTextView.setTag(vo);
			hourTextView.setOnClickListener(this);

			addView(hourTextView);

		}
		invalidate();
	}

	public class ExtraTouchableTextView extends TextView {

		public ExtraTouchableTextView(Context context) {
			super(context);
		}

		@Override
		public void getHitRect(Rect outRect) {
			outRect.set(getLeft() - TOUCH_DELEGATE, getTop() - TOUCH_DELEGATE,
					getRight() + TOUCH_DELEGATE, getTop() + TOUCH_DELEGATE);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// scale the view to be a square. Cause we want to display a circular
		// clock and it better be contained in a square shaped container
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);

		int chosenDimension = Math.min(chosenWidth, chosenHeight);

		setMeasuredDimension(chosenDimension + getPaddingLeft()
				+ getPaddingRight() + RIM_PADDING + BACKGROUND_PADDING,
				chosenDimension + getPaddingTop() + getPaddingBottom()
						+ RIM_PADDING + BACKGROUND_PADDING);

	}

	private int chooseDimension(int mode, int size) {

		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY)
			return size;
		else
			return MINIMUM_SIZE;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		rimRect.set(getPaddingLeft() + BACKGROUND_PADDING, getPaddingTop()
				+ BACKGROUND_PADDING, getMeasuredWidth() - getPaddingRight()
				- BACKGROUND_PADDING, getMeasuredHeight() - getPaddingBottom()
				- BACKGROUND_PADDING);

		backRect.set(getPaddingLeft(), getPaddingTop(), getMeasuredWidth()
				- getPaddingRight(), getMeasuredHeight() - getPaddingBottom());

		centerRect.set(getMeasuredWidth() / 2 - HANDLE_CIRCLE_RADIUS,
				getMeasuredHeight() / 2 - HANDLE_CIRCLE_RADIUS,
				getMeasuredWidth() / 2 + HANDLE_CIRCLE_RADIUS,
				getMeasuredHeight() / 2 + HANDLE_CIRCLE_RADIUS);

		if (selectedViewIndex == -1) {
			View child = getChildAt(getChildCount() - 1);
			selectionRect.set(child.getLeft() + child.getWidth() / 2
					- SELECTION_CIRCLE_RADIUS,
					child.getTop() + child.getHeight() / 2
							- SELECTION_CIRCLE_RADIUS,
					child.getRight() - child.getWidth() / 2
							+ SELECTION_CIRCLE_RADIUS, child.getBottom()
							- child.getHeight() / 2 + SELECTION_CIRCLE_RADIUS);
		}

		int childCount = getChildCount();

		int radius = getMeasuredWidth() / 2 - getPaddingLeft()
				- getPaddingTop() - RIM_PADDING - BACKGROUND_PADDING;

		/*-  its a adjustment to start our clock from the 12th position -*/

		int maxChildWidth = 0;
		for (int i = 0; i < childCount; i++) {
			if (getChildAt(i).getMeasuredWidth() > maxChildWidth)
				maxChildWidth = getChildAt(i).getMeasuredWidth();
		}

		for (int i = 0; i < childCount; i++) {

			View child = getChildAt(i);

			HourMinuteVo vo = (HourMinuteVo) child.getTag();
			final int childMeasuredWidth = child.getMeasuredWidth();
			final int childMeasuredHeight = child.getMeasuredHeight();

			int rotateAngle = (int) ((START_ANGLE + (i * DEGREES_PER_NICK)));

			int x = (int) (radius * Math.cos(Math.toRadians(rotateAngle)))
					+ radius - childMeasuredWidth / 2;
			int y = (int) (radius * Math.sin(Math.toRadians(rotateAngle)))
					+ radius - childMeasuredHeight / 2;
			vo.degrees = rotateAngle;

			int childLeft = x + getPaddingLeft() + getPaddingRight()
					+ RIM_PADDING + BACKGROUND_PADDING;
			int childTop = y + getPaddingTop() + getPaddingBottom()
					+ RIM_PADDING + BACKGROUND_PADDING;
			int childRight = childLeft + childMeasuredWidth;
			int childBottom = childTop + childMeasuredHeight;

			maxChildWidth = Math.max(childRight - childLeft, maxChildWidth);

			child.setTag(vo);

			child.layout(childLeft, childTop, childLeft + maxChildWidth,
					childBottom);

		}
		invalidate();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		View child;

		final int action = MotionEventCompat.getActionMasked(ev);

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			if (isTouchingChild(ev)) {
				childIndex = getTouchingChildIndex(ev);
				child = getChildAt(childIndex);
				if (childIndex != -1) {

					selectionRect.set(child.getLeft() + child.getWidth() / 2
							- SELECTION_CIRCLE_RADIUS,
							child.getTop() + child.getHeight() / 2
									- SELECTION_CIRCLE_RADIUS, child.getRight()
									- child.getWidth() / 2
									+ SELECTION_CIRCLE_RADIUS,
							child.getBottom() - child.getHeight() / 2
									+ SELECTION_CIRCLE_RADIUS);

					selectedViewIndex = childIndex;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:

			// see if user is touching any child views here, if yes draw
			// selection over the child

			childIndex = getTouchingChildIndex(ev);
			child = getChildAt(childIndex);
			if (childIndex != -1) {

				selectionRect.set(child.getLeft() + child.getWidth() / 2
						- SELECTION_CIRCLE_RADIUS,
						child.getTop() + child.getHeight() / 2
								- SELECTION_CIRCLE_RADIUS, child.getRight()
								- child.getWidth() / 2
								+ SELECTION_CIRCLE_RADIUS, child.getBottom()
								- child.getHeight() / 2
								+ SELECTION_CIRCLE_RADIUS);

				selectedViewIndex = childIndex;

				invalidate();
			}

			break;

		case MotionEvent.ACTION_UP:

			if (childIndex != -1) {
				child = getChildAt(childIndex);
				if (!isSelectingMinutes) {
					if (mTimeSelectedListener != null)
						mTimeSelectedListener.onHourSelected(Integer
								.parseInt(((TextView) child).getText()
										.toString()));
					else
						Toast.makeText(
								getContext(),
								"Hours : "
										+ ((TextView) child).getText()
												.toString(), Toast.LENGTH_SHORT)
								.show();
					hours = Integer.parseInt(((TextView) child).getText()
							.toString());
					selectedViewIndex = -1;
					showMinutesPicker();
				} else {
					if (mTimeSelectedListener != null)
						mTimeSelectedListener.onMinutesSelected(Integer
								.parseInt(((TextView) child).getText()
										.toString()));
					else
						Toast.makeText(
								getContext(),
								"Minutes : "
										+ ((TextView) child).getText()
												.toString(), Toast.LENGTH_SHORT)
								.show();
					minutes = Integer.parseInt(((TextView) child).getText()
							.toString());
					selectedViewIndex = -1;
				}
			}
			break;
		}
		return false;
	}

	private boolean isTouchingChild(MotionEvent ev) {
		float touchX = ev.getX();
		float touchY = ev.getY();
		for (int i = 0; i < getChildCount(); i++) {
			View viewChild = getChildAt(i);
			if (touchX > viewChild.getLeft() - SELECTION_CIRCLE_RADIUS
					- TOUCH_DELEGATE
					&& touchX < viewChild.getRight() + SELECTION_CIRCLE_RADIUS
							+ TOUCH_DELEGATE
					&& touchY > viewChild.getTop() - SELECTION_CIRCLE_RADIUS
							- TOUCH_DELEGATE
					&& touchY < viewChild.getBottom() + SELECTION_CIRCLE_RADIUS
							+ TOUCH_DELEGATE) {
				return true;
			}
		}

		return false;
	}

	private int getTouchingChildIndex(MotionEvent ev) {

		float touchX = ev.getX();
		float touchY = ev.getY();

		/*-  angleInDegrees = atan2(deltaY , deltaX) * 180 / PI		 -*/

		float deltaY = (touchY - getMeasuredHeight() / 2);
		float deltaX = (touchX - getMeasuredWidth() / 2);

		double degree = Math.toDegrees(Math.atan2(deltaY, deltaX));
		if (degree < 0) {
			degree = 360 + degree;
		}

		degree += START_ANGLE;

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			HourMinuteVo vo = (HourMinuteVo) child.getTag();
			if (degree > (vo.degrees - ANGLE_ADJUSTMENT)
					&& degree < (vo.degrees + ANGLE_ADJUSTMENT))
				return (i + 2) % 12;
		}
		return -1;
	}

	private class HourMinuteVo {

		private String text;
		private int degrees;

		@Override
		public String toString() {
			return "HourMinuteVo [text=" + text + ", degrees=" + degrees + "]";
		}

	}

	public void showMinutesPicker() {

		ArrayList<ObjectAnimator> anims = new ArrayList<ObjectAnimator>();
		for (int i = 0; i < getChildCount(); i++) {
			TextView textView = (TextView) getChildAt(i);
			String text = "";
			if ((i + 1) * 5 < 9)
				text = "0" + (i + 1) * 5;
			else if ((i + 1) * 5 > 9 && (i + 1) * 5 < 60)
				text = "" + (i + 1) * 5;
			else
				text = "00";
			textView.setText(text);

			textView.setAlpha(0);

			getAnims(anims, textView);
		}

		Animator.animateObjects(200, 200, Animator.PLAY_TOGETHER,
				Animator.ACCELERATOR_INTERPOLATOR, null, anims);
		invalidate();

		isSelectingMinutes = true;
	}

	public void showHoursPicker() {

		ArrayList<ObjectAnimator> anims = new ArrayList<ObjectAnimator>();
		for (int i = 0; i < getChildCount(); i++) {
			TextView textView = (TextView) getChildAt(i);
			textView.setText("" + (i + 1));

			textView.setAlpha(0);

			getAnims(anims, textView);
		}

		Animator.animateObjects(200, 200, Animator.PLAY_TOGETHER,
				Animator.ACCELERATOR_INTERPOLATOR, null, anims);
		invalidate();

		isSelectingMinutes = false;
	}

	private void getAnims(ArrayList<ObjectAnimator> anims, View view) {

		ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);

		ObjectAnimator anim2X = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
		ObjectAnimator anim2Y = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);

		ObjectAnimator anim3X = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
		ObjectAnimator anim3Y = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
		anims.add(anim);
		anims.add(anim2X);
		anims.add(anim2Y);
		anims.add(anim3X);
		anims.add(anim3Y);

	}

	@Override
	public void onClick(View v) {

		if (v instanceof ExtraTouchableTextView) {
			ExtraTouchableTextView textView = (ExtraTouchableTextView) v;

			if (!isSelectingMinutes) {
				if (mTimeSelectedListener != null)
					mTimeSelectedListener.onHourSelected(Integer
							.parseInt(textView.getText().toString()));
				else
					Toast.makeText(getContext(),
							"Hours : " + textView.getText().toString(),
							Toast.LENGTH_SHORT).show();
				showMinutesPicker();
				selectedViewIndex = -1;
			} else {
				if (mTimeSelectedListener != null)
					mTimeSelectedListener.onMinutesSelected(Integer
							.parseInt(textView.getText().toString()));
				else
					Toast.makeText(getContext(),
							"Minutes: " + textView.getText().toString(),
							Toast.LENGTH_SHORT).show();
				selectedViewIndex = -1;

			}
		}
	}
}
