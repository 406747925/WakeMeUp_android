package cn.jlu.ge.dreamclock.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.activity.FriendListActivity;
import cn.jlu.ge.dreamclock.activity.LoginActivity;
import cn.jlu.ge.dreamclock.activity.WeatherCitiesSettingActivity;

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
		super.onActivityCreated(savedInstanceState);
		
		MenuAdapter menuAdapter = new MenuAdapter(getActivity());
		menuAdapter.add(new MenuItem(R.string.sliding_menu_user_setting, R.drawable.ic_launcher));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_friends_setting, R.drawable.default_avatar));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_weather_setting, R.drawable.alarm_off));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_communicate, R.drawable.tel));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_quit, R.drawable.alarm_on) );
		
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
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		if (position == 1) {
			Intent i = new Intent(getActivity(), FriendListActivity.class);
			startActivity(i);
		} else if ( position == 2 ) {
			Intent intent = new Intent(getActivity(), WeatherCitiesSettingActivity.class);
			startActivity(intent);
		} else if ( position == 3 ) {
			
		} else if ( position == 4 ) {
			setUserInfoDefaultToQuit();
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}

		super.onListItemClick(l, v, position, id);
	}

	public void setUserInfoDefaultToQuit() {
		SharedPreferences appInfo = getActivity().getSharedPreferences(Const.APP_INFO_PREFERENCE, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = appInfo.edit();
		editor.putBoolean(Const.USER_LOG_IN_OR_NOT, false);
		editor.putBoolean(Const.USER_SIGN_IN_OR_NOT, false);
		editor.putString(Const.USER_AVATAR_URL, "default");
		editor.putInt(Const.USER_BEEN_JEER_NUM, -1);
		editor.putInt(Const.USER_CONTINUOUS_SIGN_IN_DAYS, -1);
		editor.putInt(Const.USER_SCORE, -1);
		editor.putInt(Const.USER_SIGN_IN_STRANGERS_NUM, -1);
		editor.putString(Const.USER_ID, "none");
		editor.putInt(Const.SIGN_IN_RANK_NUM, Const.DEFAULT_SIGN_IN_RANK_NUM);
		editor.putInt(Const.USER_RANK, Const.DEFAULT_USER_RANK);
		
		SimpleDateFormat timeFm = new SimpleDateFormat("yyyy-MM-dd");
		String timeStr = timeFm.format(new Date()) + " 04:00";
		editor.putString(Const.GET_USERS_LIST_LAST_TIME, timeStr);
		editor.commit();
		timeFm = null;
	}
	
	public class MenuAdapter extends ArrayAdapter<MenuItem> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null && position != 0) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.row, null);
				ImageView icon = (ImageView) convertView
						.findViewById(R.id.row_icon);
				icon.setImageResource(getItem(position).iconRes);
				TextView title = (TextView) convertView
						.findViewById(R.id.row_title);
				title.setText(getItem(position).tagRes);
			} else {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.user_row, null);
				ImageView icon = (ImageView) convertView
						.findViewById(R.id.user_head);
				icon.setImageResource(R.drawable.originalhead);
				TextView title = (TextView) convertView
						.findViewById(R.id.user_name);
				title.setText(getItem(position).tagRes);
			}
			return convertView;
		}
	}
}
