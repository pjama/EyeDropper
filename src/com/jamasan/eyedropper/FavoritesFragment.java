package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class FavoritesFragment extends Fragment {

	private ListView mListFavorites;
	private SQLiteManager mSQL; 
	
	private CustomAdapter mCustomAdapter;
	private ArrayList<CustomListItem> mListRowItems;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.favorites_fragment, null);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListFavorites = (ListView)getActivity().findViewById(R.id.favorites_list);
		mListRowItems = new ArrayList<CustomListItem>();
		mCustomAdapter = new CustomAdapter(getActivity(), R.layout.row_custom, mListRowItems);
		mListFavorites.setAdapter(mCustomAdapter);
		mListFavorites.setOnItemClickListener(onItemClickListener);
		mListFavorites.setOnItemLongClickListener(onItemLongClickListener);
		listFavorites();
	}
	
	private void listFavorites() {
		if(mSQL == null) {
			mSQL = new SQLiteManager(getActivity());
		}
		ArrayList<ColorSample> colors = mSQL.getColors();
		
		TextView textNoFavorites = (TextView)getActivity().findViewById(R.id.favorites_unavailable);
		if(colors.isEmpty()) {
			textNoFavorites.setVisibility(View.VISIBLE);
		} else {
			textNoFavorites.setVisibility(View.GONE);
			for(ColorSample color : colors) {
				mListRowItems.add(new CustomListItem(color));
			}
			((CustomAdapter)mListFavorites.getAdapter()).notifyDataSetChanged();
		}
	}
	
	private void deleteFavorite(int colorId) {
		mSQL.deleteColor(colorId);
	}
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			CustomAdapter adapter = (CustomAdapter)parent.getAdapter();
			CustomListItem item = adapter.getItem(pos);
			ColorPoint color = item.getColor();
			
			DetailFragment fragment = new DetailFragment();
			fragment.setArguments(color.toBundle());
			
			FragmentManager manager = getActivity().getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.main_fragment, fragment);
			transaction.commit();
			((FullscreenActivity)getActivity()).setDetailedView(fragment);
		}
	};
	
	OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
			CustomAdapter adapter = (CustomAdapter)parent.getAdapter();
			CustomListItem item = adapter.getItem(pos);
			int colorId = item.getColorId();
			deleteFavorite(colorId);
			listFavorites();
			return true;
		}
	};
}
