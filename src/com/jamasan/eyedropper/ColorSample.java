package com.jamasan.eyedropper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;

public class ColorSample extends ColorPoint {
	private long mId;
	private Date mDateCaptured;
	private String mSource;
	
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	
	public ColorSample(int argb) {
		super(argb | (0xFF << 24));
	}
	
	public ColorSample(int r, int g, int b) {
		super(r, g, b);
	}
	
	public ColorSample(Bundle args) {
		super(args.getInt("color_argb"));
		this.setName(args.getString("name"));
		
		if (args.containsKey("color_id")) {
			setId(args.getLong("color_id"));
		} else {
			setId(-1);
		}
		String strDate = args.getString("date_captured");
		Date date;
		if (strDate == null) {
			Calendar cal = Calendar.getInstance();
			this.mDateCaptured = cal.getTime();
		} else {
			try {
				SimpleDateFormat formatter;
				formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
				date = formatter.parse(strDate);
				this.mDateCaptured = date;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		this.mSource = args.getString("source");
	}
	
	public long getId() {
		return mId;
	}
	
	public ColorSample setId(long id) {
		mId = id;
		return this;
	}
	
	public Date getDateCaptured() {
		return this.mDateCaptured;
	}
	
	public String getDateCapturedString() {
		String dateFormat = DATE_FORMAT;
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
		if (mDateCaptured == null) {
			return formatter.format(Calendar.getInstance().getTime());
		} else {
			return formatter.format(mDateCaptured);
		}
	}
	
	public ColorSample setDate(Date date) {
		this.mDateCaptured = date;
		return this;
	}
	
	public String getSource() {
		return this.mSource;
	}
	
	public ColorSample setSource(String source) {
		this.mSource = source;
		return this;
	}
	
	public Bundle toBundle() {
		Bundle bundle = super.toBundle();
		bundle.putLong("color_id", this.getId());
		bundle.putString("date_captured", this.getDateCapturedString());
		bundle.putString("source", this.getSource());
		return bundle;
	}
}
