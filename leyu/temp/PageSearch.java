package com.ezhair;

import java.util.ArrayList;
import java.util.List;

import com.ezhair.EasyListView.OnOverScrollListener;
import com.ezhair.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PageSearch extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_search, container, false);
		inflater.inflate(R.layout.page_search, container, false);
		EasyListView list = (EasyListView) rootView.findViewById(R.id.listView);
		final View title = rootView.findViewById(R.id.title_01);
		list.setAdapter(new SearchAdapter());;
		list.setOnOverScrollListener(new OnOverScrollListener(){

			@Override
			public void onUpOverscrolled() {
				title.setVisibility(View.VISIBLE);
				
			}

			@Override
			public void onDownOverscrolled() {
				title.setVisibility(View.GONE);
				
			}});
		return rootView;
	}

	class SearchAdapter extends BaseAdapter {

		private List<Item> m_Data = new ArrayList<Item>();

		class Item {
			int m_Type;
			String m_Title;
			String m_Desc;
			int m_Value;

			Item(String title, String Desc) {
				m_Title = title;
				m_Desc = Desc;
			}

			Item(int type, String title, String Desc) {
				m_Title = title;
				m_Desc = Desc;
			}
		}

		SearchAdapter() {
			m_Data.add(new Item("品牌", "日式威廉"));
			m_Data.add(new Item("縣市", "日式威廉"));
			m_Data.add(new Item("品牌", "日式威廉"));
			m_Data.add(new Item("品牌", "日式威廉"));
			m_Data.add(new Item("品牌", "日式威廉"));
			m_Data.add(new Item("品牌", "日式威廉"));
			m_Data.add(new Item("品牌", "日式威廉"));
			m_Data.add(new Item("品牌", "日式威廉"));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 16;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_search_01, parent, false);
	
			}
			if (position % 2 == 1) {
				convertView.setBackgroundColor(getActivity().getResources().getColor(android.R.color.white));
			}else{
				convertView.setBackgroundColor(getActivity().getResources().getColor(R.color.gray));
			}
			return convertView;
		}
		
		class  ViewHolder{
			ImageView img;
			TextView  sitekey;
			TextView partname;
			TextView price;
			TextView value;
			TextView quantity_sold;
			TextView end_date;
			TextView jiantou;
		}
	}
}