package com.jamasan.eyedropper;

import java.util.HashMap;
import java.util.Map;

public class ColorRAL {	
	
	private HashMap<String, ColorPoint> ralColors;
	
	public ColorRAL() {
		ralColors = new HashMap<String, ColorPoint>();
		ralColors.put("1000", new ColorPoint(203, 185, 141, "Green beige"));
		ralColors.put("1001", new ColorPoint(204, 176, 137, "Beige"));
		ralColors.put("1002", new ColorPoint(205, 169, 116, "Sand Yellow"));
		ralColors.put("3001", new ColorPoint(138, 43, 39, "Signal Red"));
		ralColors.put("6032", new ColorPoint(36, 113, 70, "Signl Green"));
	}
	
	public ColorPoint getClosestColor(int r, int g, int b) {
		ColorPoint refPoint = new ColorPoint(r, g, b, null);
		ColorPoint closestPoint = null;
		double closestDistance = Double.MAX_VALUE;
		for(Map.Entry<String, ColorPoint> entry : this.ralColors.entrySet()) {
			ColorPoint p = entry.getValue();
			double distance = p.getDistance(refPoint);
			if (distance < closestDistance) {
				closestDistance = distance;
				closestPoint = p;
			}
		}
		if (closestPoint == null) return null;
		return closestPoint;
	}
}
