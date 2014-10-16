package com.roomies.clockedtimepickersample;

import com.roomies.clockedtimepickersample.TimePickerDialogFragment.TimePickerDialogListener;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		TimePickerDialogListener {

	private TextView selectedTimeTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		selectedTimeTv = (TextView) findViewById(R.id.selectedTime);
		selectedTimeTv.setTypeface(Typeface.createFromAsset(getAssets(),
				"Roboto-Thin.ttf"));

		selectedTimeTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				TimePickerDialogFragment fragment = new TimePickerDialogFragment();
				fragment.show(getSupportFragmentManager(),
						"time_picker_dialog_fragment");
			}
		});

	}

	@Override
	public void onTimeSelectedFromDialog(String hour, String minutes,
			String denominator) {

		selectedTimeTv.setText(hour + ":" + minutes + " " + denominator);
	}

}
