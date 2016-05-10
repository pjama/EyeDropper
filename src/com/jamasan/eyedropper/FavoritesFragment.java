package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FavoritesFragment extends Fragment {

	private ListView mListFavorites;
	private SQLiteManager mSQL; 
	
	private CustomAdapter mAdapter;
	private ArrayList<CustomListItem> mListRowItems;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.favorites_fragment, null);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListFavorites = (ListView)getActivity().findViewById(R.id.favorites_list);
		mListRowItems = new ArrayList<CustomListItem>();
		mAdapter = new FavoriteListAdapter(getActivity(), R.layout.row_favorite, mListRowItems, onClickDelete);
		mListFavorites.setAdapter(mAdapter);
		mListFavorites.setOnItemClickListener(onItemClickListener);
		ImageView sliderNav = ((ImageView)getActivity().findViewById(R.id.favorites_drawer_icon));
		sliderNav.setOnClickListener(onClickListener);
		listFavorites();
	}
	
	private void listFavorites() {
		if (mSQL == null) {
			mSQL = new SQLiteManager(getActivity());
		}
		mListRowItems.clear();
		ArrayList<ColorSample> colors = mSQL.getColors();
		TextView textNoFavorites = (TextView)getActivity().findViewById(R.id.favorites_unavailable);
		if (colors.isEmpty()) {
			textNoFavorites.setVisibility(View.VISIBLE);
		} else {
			textNoFavorites.setVisibility(View.GONE);
			for(ColorSample color : colors) {
				CustomListItem item = new CustomListItem(color);
				item.setDescription(color.getDateCapturedString());
				mListRowItems.add(item);
			}
			((CustomAdapter)mListFavorites.getAdapter()).notifyDataSetChanged();
		}
	}
	
	private void deleteFavorite(long colorId) {
		mSQL.deleteColor(colorId);
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.favorites_drawer_icon:
				((FullscreenActivity)getActivity()).showDrawerMenu(true);
				break;
			}
		}
	};
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			CustomAdapter adapter = (CustomAdapter)parent.getAdapter();
			CustomListItem item = adapter.getItem(pos);
			ColorSample color = (ColorSample)item.getColor();
			
			DetailFragment fragment = new DetailFragment();
			Bundle args = color.toBundle();
			fragment.setArguments(args);
			
			((FullscreenActivity)getActivity()).setActiveFragment(fragment);
		}
	};
	
	OnClickListener onClickDelete = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Long favoriteId = (Long)v.getTag();
			deleteFavorite(favoriteId);
			listFavorites();
		}
	};
}
