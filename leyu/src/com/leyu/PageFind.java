package com.leyu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PageFind extends Fragment {
	static final String TAG = PageFind.class.getSimpleName();
	// Data
	private String mHeadPic = "http://www.sucaifengbao.com/uploadfile/photo/meinvtupianbizhi/meinvtupianbizhi_813_030.jpg";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_recommand, container, false);

		int width, height;
		// = (int)
		// (PageHome.this.getActivity().getResources().getDisplayMetrics().density
		// * 189.33);
		width = PageFind.this.getActivity().getResources().getDisplayMetrics().widthPixels;
		height = (int) (PageFind.this.getActivity().getResources().getDisplayMetrics().density * 140);
		ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mHeadPic))
				.setResizeOptions(new ResizeOptions(width, height))
				.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true)
				.build();
		SimpleDraweeView image = ((SimpleDraweeView) rootView.findViewById(R.id.headline));
		DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request)
				.setOldController(image.getController()).build();
		image.setController(controller);

		ListView list = ((ListView) rootView.findViewById(R.id.list));
		list.setDivider(null);
		ViewGroup footer = new LinearLayout(PageFind.this.getActivity());
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (46.67 * PageFind.this.getResources().getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		list.addFooterView(footer);
		list.setAdapter(new LeyuAdapter());

		return rootView;
	}

	class LeyuAdapter extends BaseAdapter {

		private List<Item> m_Data = new ArrayList<Item>();

		class Item {
			String mTitle;
			String mUrl;

			Item(String title, String url) {
				mTitle = title;
				mUrl = url;
			}
		}

		LeyuAdapter() {

			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return m_Data.size();
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
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.listitem_recommand, parent, false);
				holder.mTitle = (TextView) convertView.findViewById(R.id.headline);
				holder.image = (SimpleDraweeView) convertView.findViewById(R.id.headline_value);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.mTitle.setText(m_Data.get(position).mTitle);
			String uriBase = "http://www.sucaifengbao.com/uploadfile/photo/meinvtupianbizhi/meinvtupianbizhi_813_";
			DecimalFormat df = new DecimalFormat("'0'.jpg");
			final Uri uri = Uri.parse(uriBase + df.format(position + 20));
			//
			int width, height;
			width = height = (int) (PageFind.this.getActivity().getResources().getDisplayMetrics().density * 115);
			ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
					.setResizeOptions(new ResizeOptions(width, height))
					.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true)
					.build();
			DraweeController controller = Fresco.newDraweeControllerBuilder()
					.setImageRequest(request).setOldController(holder.image.getController())
					.build();
			holder.image.setController(controller);

			return convertView;
		}

		class ViewHolder {
			TextView mTitle;
			SimpleDraweeView image;
		}
	}
}