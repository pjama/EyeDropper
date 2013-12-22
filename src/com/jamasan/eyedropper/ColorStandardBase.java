package com.jamasan.eyedropper;

import java.util.HashMap;
import java.util.Map;

public abstract class ColorStandardBase {

	private String mStandardName;
	protected HashMap<String, ColorPoint> mColors;
	
	public ColorStandardBase(String standardName) {
		this.mStandardName = standardName;
		mColors = new HashMap<String, ColorPoint>();
	}
	
	public ColorPoint getClosestColor(ColorPoint color) {
		ColorPoint closestPoint = null;
		double closestDistance = Double.MAX_VALUE;
		for(Map.Entry<String, ColorPoint> entry : this.mColors.entrySet()) {
			ColorPoint p = entry.getValue();
			double distance = p.getDistance(color);
			if (distance < closestDistance) {
				closestDistance = distance;
				closestPoint = p;
				closestPoint.setCode(entry.getKey());
			}
		}
		if (closestPoint == null) { 
			return null;
		}
		closestPoint.setDescription(this.mStandardName + " " + closestPoint.getCode());
		return closestPoint;
	}
}
