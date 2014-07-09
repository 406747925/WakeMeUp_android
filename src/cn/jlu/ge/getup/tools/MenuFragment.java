package cn.jlu.ge.getup.tools;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jlu.ge.getup.R;

public class MenuFragment extends ListFragment {

	public int screenHeight;
	public int screenWidth;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub	    
		return inflater.inflate(R.layout.menu_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int widthPixels= dm.widthPixels;
//		int heightPixels= dm.heightPixels;
//		float density = dm.density;
//		screenWidth = (int) (widthPixels * density) ;
//		screenHeight = (int) (heightPixels * density) ;
		
		super.onActivityCreated(savedInstanceState);
		
		MenuAdapter menuAdapter = new MenuAdapter(getActivity());
		menuAdapter.add(new MenuItem(R.string.sliding_menu_user_setting, R.drawable.ic_launcher));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_alarm_setting, R.drawable.ic_launcher));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_weather_setting, R.drawable.ic_launcher));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_model_setting, R.drawable.ic_launcher));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_share, R.drawable.ic_launcher));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_communicate, R.drawable.ic_launcher));
		
		setListAdapter(menuAdapter);
	}
	
	private class MenuItem {
		public int tagRes;
		public int iconRes;
		public MenuItem(int tagRes, int iconRes) {
			this.tagRes = tagRes;
			this.iconRes = iconRes;
		}
	}
	

	
	public class MenuAdapter extends ArrayAdapter<MenuItem> {
		
		public MenuAdapter(Context context) {
			super(context, 0);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null && position != 0) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
				ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
				icon.setImageResource(getItem(position).iconRes);
				TextView title = (TextView) convertView.findViewById(R.id.row_title);
				title.setText(getItem(position).tagRes);
			} else {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_row, null);
				ImageView icon = (ImageView) convertView.findViewById(R.id.user_head);
				icon.setImageResource(getItem(position).iconRes);
				TextView title = (TextView) convertView.findViewById(R.id.user_name);
				title.setText(getItem(position).tagRes);
			}
			return convertView;
		}
	}
}
