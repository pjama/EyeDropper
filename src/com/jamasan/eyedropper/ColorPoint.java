package com.jamasan.eyedropper;

import android.os.Bundle;

public class ColorPoint {
	private int a;
	private int r;
	private int g;
	private int b;
	private String mName;
	private String mDescription;
	private String mCode;
	
	public ColorPoint(int r, int g, int b, String name) {
		this.a = 0xFF;
		this.r = r;
		this.g = g;
		this.b = b;
		this.mName = name;
		this.mDescription = "#" + this.getHex();
	}
	
	public ColorPoint(int r, int g, int b) {
		this.a = 0xFF;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public ColorPoint(int argb, String name) {
		this.mName = name;
		this.a = 0xFF;
		this.r = (argb >> 16) & 0xFF;
		this.g = (argb >>  8) & 0xFF;
		this.b = (argb >>  0) & 0xFF;
	}
	
	public ColorPoint(int argb) {
		this.a = (argb >> 24) & 0xFF;
		this.r = (argb >> 16) & 0xFF;
		this.g = (argb >>  8) & 0xFF;
		this.b = (argb >>  0) & 0xFF;
	}
	
	public ColorPoint getWebSafeColor() {
		int rw = 51 * ((r+25)/51);
		int gw = 51 * ((g+25)/51);
		int bw = 51 * ((b+25)/51);
		ColorPoint colorWebSafe = new ColorPoint(rw, gw, bw, "Web-Safe RGB");
		return colorWebSafe; 
	}
	
	public int getARGB() {
		return ((a & 0xFF)<<24) + ((r & 0xFF)<<16) + ((g & 0xFF)<<8) + (b & 0xFF);
	}
	
	public int getR() {
		return this.r;
	}
	
	public int getG() {
		return this.g;
	}
	
	public int getB() {
		return this.b;
	}
	
	public String getRGB() {
		return String.format("R:%d G:%d B:%d", this.r, this.g, this.b);
	}
	public String getHex() {
		String hex = String.format("%06X", this.getARGB() & 0xFFFFFF);
		return hex;
	}
	
	public ColorPoint setName(String name) {
		this.mName = name;
		return this;
	}
	
	public String getName() {
		return this.mName;
	}
	
	public ColorPoint setCode(String code) {
		this.mCode = code;
		return this;
	}
	
	public String getCode() {
		return this.mCode;
	}
	
	public ColorPoint setDescription(String description) {
		this.mDescription = description;
		return this;
	}
	
	public String getDescription() {
		return this.mDescription;
	}
	
	public double getDistance(ColorPoint p) {
		double dr2 = Math.pow(this.r - p.r, 2);
		double dg2 = Math.pow(this.g - p.g, 2);
		double db2 = Math.pow(this.b - p.b, 2);
		double distance = Math.sqrt(dr2 + dg2 + db2);
		return distance;
	}
	
	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putInt("color_argb", this.getARGB());
		bundle.putString("name", this.getName());
		
		return bundle;
	}
	@Override
	public String toString() {
		return this.getHex();
	}
}
