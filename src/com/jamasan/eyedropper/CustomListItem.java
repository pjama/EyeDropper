package com.jamasan.eyedropper;

import android.text.Html;
import android.text.Spanned;

public class CustomListItem {
	
	private ColorPoint mColor;

	public CustomListItem() { }
	
	public CustomListItem(ColorPoint color) {
		this.mColor = color;
	}
	
	public CustomListItem(ColorPoint color, String title, String desc) {
		this.mColor = color;
		this.mItemTitle = Html.fromHtml(title);
		this.mItemDescription = desc;
	}
	
	public CustomListItem(ColorPoint color, Spanned title, String desc) {
		this.mColor = color;
		this.mItemTitle = title;
		this.mItemDescription = desc;
	}
	
	private int mIconSize;
	private Spanned mItemTitle;
	private String mItemDescription;
	
	
	public CustomListItem setIconSize(int size) {
		mIconSize = size;
		return this;
	}
	
	public int getIconSize() {
		return this.mIconSize;
	}
	
	public CustomListItem setTitle(String title) {
		mItemTitle = Html.fromHtml(title);
		return this;
	}
	
	public CustomListItem setTitle(Spanned title) {
		mItemTitle = title;
		return this;
	}
	
	public String getTitle() {
		return mItemTitle.toString();
	}
	
	public CustomListItem setDescription(String description) {
		mItemDescription = description;
		return this;
	}
	
	public String getDescription() {
		return this.mItemDescription;
	}

	public void setColor(ColorPoint color) {
		this.mColor = color;
	}
	
	public ColorPoint getColor() {
		return mColor;
	}
	
}
