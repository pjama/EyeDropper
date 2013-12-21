package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<CustomListItem> {

	private Context mContext;
	private ArrayList<CustomListItem> mItems;
	
	public CustomAdapter(Context context, int textViewResourceId, ArrayList<CustomListItem> objects) {
		super(context, textViewResourceId, objects);
		
		mContext = context;
		mItems = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_custom, null);
        } else {
        	v = convertView;
        }
        
        CustomListItem item = mItems.get(position);
        
        if (item != null) {
        	
        	ImageView tImage = (ImageView) v.findViewById(R.id.row_image);
        	TextView tTitle = (TextView) v.findViewById(R.id.rowTitle);
        	TextView tDescription = (TextView) v.findViewById(R.id.rowDescription);
        	
        	if (tImage != null) {
    			tImage.setBackgroundColor(item.getColor().getARGB());
        	}
        	if (tTitle != null) {
        		tTitle.setText(item.getTitle());
        	}
        	if(tDescription != null){
        		tDescription.setText(item.getDescription());
        	}
        	if(item.getIconSize() > 0) { 
    			
    			ViewGroup.LayoutParams params = tImage.getLayoutParams();
        		params.height = Utils.getDip(mContext, item.getIconSize());
        		params.width = Utils.getDip(mContext, item.getIconSize());
        		tImage.setLayoutParams(params);
        	}
        }
        return v;
	}
}
