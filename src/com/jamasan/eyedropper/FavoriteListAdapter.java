package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.content.Context;
import android.view.View.OnClickListener;

public class FavoriteListAdapter extends CustomAdapter {
	
	public FavoriteListAdapter(Context context, int layoutResourceId, ArrayList<CustomListItem> objects) {
		super(context, layoutResourceId, objects);
	}
	
	public FavoriteListAdapter(Context context, int layoutResourceId, ArrayList<CustomListItem> objects, OnClickListener onClickDelete) {
		super(context, layoutResourceId, objects);
		mOnClickDelete = onClickDelete;
	}
}
