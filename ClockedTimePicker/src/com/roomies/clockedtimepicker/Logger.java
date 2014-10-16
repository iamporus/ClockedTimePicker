package com.roomies.clockedtimepicker;

public class Logger {

	private static final boolean LOG = true;
	public static String TAG = "~~~ TimePicker ~~~ : ";

	public static void Log(String logText) {
		if (LOG)
			System.out.println(TAG + logText);
	}
}
