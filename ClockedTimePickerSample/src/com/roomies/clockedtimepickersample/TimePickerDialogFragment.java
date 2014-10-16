package com.roomies.clockedtimepickersample;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.roomies.clockedtimepicker.Time;
import com.roomies.clockedtimepicker.TimePicker;
import com.roomies.clockedtimepicker.TimePicker.OnTimeSelectedListener;
import com.roomies.clockedtimepicker.TimeTextView;
import com.roomies.clockedtimepicker.TimeTextView.OnTimeClickedListener;

public class TimePickerDialogFragment extends DialogFragment {

	private TimeTextView mTimeTextView;
	private String denominator;
	private String hour;
	private String minutes;
	private TimePicker mTimePicker;

	public interface TimePickerDialogListener {
		void onTimeSelectedFromDialog(String hour, String minutes,
				String denominator);
	}

	public TimePickerDialogFragment() {

		// Empty constructor is always required by a DialogFragment
	}

	@SuppressLint("InlinedApi")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.time_picker_dialog_layout, null);
		ContextThemeWrapper context = new ContextThemeWrapper(getActivity(),
				android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(view).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							TimePickerDialogListener activity = (TimePickerDialogListener) getActivity();
							activity.onTimeSelectedFromDialog(hour, minutes,
									denominator);
							TimePickerDialogFragment.this.dismiss();
						} catch (ClassCastException e) {
							throw new ClassCastException(
									getActivity().toString()
											+ " must implement TimePickerDialogListener interface");
						}
					}
				});

		initViews(view);
		return alertDialogBuilder.create();
	}

	private void initViews(View view) {

		mTimeTextView = (TimeTextView) view.findViewById(R.id.timeTextView);
		mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
		mTimePicker.addHourViews(getActivity());
		setSelectedTime("AM", "12", "00");

		mTimeTextView.setOnTimeClickedListener(new OnTimeClickedListener() {

			@Override
			public void onMinutesClicked() {
				mTimePicker.showMinutesPicker();

			}

			@Override
			public void onHoursClicked() {
				mTimePicker.showHoursPicker();
			}

			@Override
			public void onDenominatorClicked() {
				TimePickerDialogFragment.this.denominator = mTimeTextView
						.switchDenominator();

			}
		});

		mTimePicker.setOnTimeSelectedListener(new OnTimeSelectedListener() {

			@Override
			public void onHourSelected(int hour) {
				Time time = new Time();
				time.setHour(hour < 10 ? "0" + hour : "" + hour);
				time.setMinutes(mTimePicker.getSelectedMinutes() == 0 ? "00"
						: "" + mTimePicker.getSelectedMinutes());
				time.setDenominator("AM");
				TimePickerDialogFragment.this.hour = time.getHour();
				mTimeTextView.setTime(time);
			}

			@Override
			public void onMinutesSelected(int minutes) {

				Time time = new Time();
				time.setHour(mTimePicker.getSelectedHours() < 10 ? "0"
						+ mTimePicker.getSelectedHours() : ""
						+ mTimePicker.getSelectedHours());
				time.setMinutes(minutes == 0 ? "00" : "" + minutes);
				time.setDenominator("AM");
				TimePickerDialogFragment.this.minutes = time.getMinutes();
				mTimeTextView.setTime(time);
			}

		});

	}

	private void setSelectedTime(String denominator, String hour, String minutes) {
		this.denominator = denominator;
		this.hour = hour;
		this.minutes = minutes;

		Time time = new Time();
		time.setHour(Integer.parseInt(hour) < 10 ? "0" + hour : "" + hour);
		time.setMinutes(minutes);
		time.setDenominator(denominator);
		mTimeTextView.setTime(time);

	}

}
