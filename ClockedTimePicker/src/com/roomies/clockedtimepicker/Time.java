package com.roomies.clockedtimepicker;

public class Time {

	private String hour;
	private String minutes;
	private String denominator;

	public Time() {

	}

	public Time(String hour, String minutes, String denominator) {
		this.hour = hour;
		this.minutes = minutes;
		this.denominator = denominator;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public String getDenominator() {
		return denominator;
	}

	public void setDenominator(String denominator) {
		this.denominator = denominator;
	}

	public String getReadableTime() {
		return hour + ":" + minutes + " " + denominator;
	}

	@Override
	public String toString() {
		return "Time [hour=" + hour + ", minutes=" + minutes + ", denominator="
				+ denominator + "]";
	}

}