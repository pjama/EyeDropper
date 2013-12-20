package com.jamasan.eyedropper;

public class Color {
	private int a;
	private int r;
	private int g;
	private int b;
	
	public Color(int r, int g, int b) {
		this.a = 0xFF;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Color(int argb) {
		this.a = (argb >> 24) & 0xFF;
		this.r = (argb >> 16) & 0xFF;
		this.g = (argb >>  8) & 0xFF;
		this.b = (argb >>  0) & 0xFF;
	}
	
	public Color getWebSafeColor() {
		int rw = 51 * ((r+25)/51);
		int gw = 51 * ((g+25)/51);
		int bw = 51 * ((b+25)/51);	
		return new Color(rw, gw, bw); 
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
	
	@Override
	public String toString() {
		return this.getHex();
	}
}
