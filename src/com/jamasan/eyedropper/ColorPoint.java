package com.jamasan.eyedropper;

public class ColorPoint {
	private int a;
	private int r;
	private int g;
	private int b;
	private String mName;
	private String mCode;
	
	public ColorPoint(int r, int g, int b, String name) {
		this.a = 0xFF;
		this.r = r;
		this.g = g;
		this.b = b;
		this.mName = name;
	}
	
	public ColorPoint(int r, int g, int b) {
		this.a = 0xFF;
		this.r = r;
		this.g = g;
		this.b = b;
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
		return new ColorPoint(rw, gw, bw); 
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
	
	public String getHex() {
		String hex = String.format("0x%06X", this.getARGB() & 0xFFFFFF);
		return hex;
	}
	
	public String getName() {
		return this.mName;
	}
	
	public double getDistance(ColorPoint p) {
		double dr2 = Math.pow(this.r - p.r, 2);
		double dg2 = Math.pow(this.g - p.g, 2);
		double db2 = Math.pow(this.b - p.b, 2);
		double distance = Math.sqrt(dr2 + dg2 + db2);
		return distance;
	}
	
	@Override
	public String toString() {
		return this.getHex();
	}
}
