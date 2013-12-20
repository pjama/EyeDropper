package com.jamasan.eyedropper;

public class Point3D {
	private double x;
	private double y;
	private double z;
	private String name;
	public Point3D(double x, double y, double z, String name) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
    }
	
	public double getX() {
		return this.x;
	}
    
	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getDistance(Point3D p) {
		double dx2 = Math.pow(this.x - p.x, 2);
		double dy2 = Math.pow(this.y - p.y, 2);
		double dz2 = Math.pow(this.z - p.z, 2);
		double distance = Math.sqrt(dx2 + dy2 + dz2);
		return distance;
	}
}
