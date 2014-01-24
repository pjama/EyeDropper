package com.jamasan.eyedropper;

import java.util.Date;

import android.os.Bundle;

public class ColorSample extends ColorPoint {

	private Date mDateCaptured;
	private String mSource;
	
	public ColorSample(int argb) {
		super(argb);
	}
	
	public ColorSample(Bundle args) {
		super(args.getInt("color_argb"));
		this.setName(args.getString("name"));
		//mDateCaptured = args.getString("date_captured");
		//mSource = args.getString("source");
	}
	
}
