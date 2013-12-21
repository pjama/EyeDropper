package com.jamasan.eyedropper;

public class CustomListItem {
	
	private ColorPoint mColor;

	public CustomListItem() { }
	
	public CustomListItem(ColorPoint color) {
		this.mColor = color;
	}
	
	private int mIconSize;
	
	public CustomListItem setIconSize(int size) {
		mIconSize = size;
		return this;
	}
	
	public int getIconSize() {
		return this.mIconSize;
	}
	
	
	public String getTitle() {
		return mColor.getName();
	}
	
	public CustomListItem setDescription(String description) {
		mColor.setDescription(description);
		return this;
	}
	
	public String getDescription() {
		return mColor.getDescription();
	}

	public void setColor(ColorPoint color) {
		this.mColor = color;
	}
	
	public ColorPoint getColor() {
		return mColor;
	}
	
}