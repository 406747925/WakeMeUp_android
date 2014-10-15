package cn.jlu.ge.dreamclock.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import cn.jlu.ge.dreamclock.activity.ChangeUserInfoActivity;
import cn.jlu.ge.dreamclock.activity.FriendListActivity;
import cn.jlu.ge.dreamclock.activity.LoginActivity;
import cn.jlu.ge.dreamclock.activity.UserCommunicateActivity;
import cn.jlu.ge.dreamclock.activity.WeatherCitiesSettingActivity;
import cn.jlu.ge.knightView.CircleImageView;

public class MenuFragment extends ListFragment {

	public int screenHeight;
	public int screenWidth;
	private String userName;
	private String avatarUrl;
	BitmapCache bitmapCache;
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
		
		bitmapCache = new BitmapCache(getActivity().getApplicationContext());
		
		getUserInfo();
		
		MenuAdapter menuAdapter = new MenuAdapter(getActivity());
		menuAdapter.add(new MenuItem(userName, avatarUrl));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_friends_setting, R.drawable.friends_setting));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_weather_setting, R.drawable.weather_setting));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_communicate, R.drawable.contact_us));
		menuAdapter.add(new MenuItem(R.string.sliding_menu_quit, R.drawable.quit_app));
		
		setListAdapter(menuAdapter);
	}
	
	private class MenuItem {
		public int tagRes;
		public int iconRes;
		public String tagStr;
		public String iconStr;
		public MenuItem(int tagRes, int iconRes) {
			this.tagRes = tagRes;
			this.iconRes = iconRes;
			this.tagStr = null;
			this.iconStr = null;
		}
		
		public MenuItem(String tagStr, String iconStr) {
			this.tagStr = tagStr;
			this.iconStr = iconStr;
			this.tagRes = 0;
			this.iconRes = 0;
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		if ( position == 0 ) {
			Intent intent = new Intent(getActivity(), ChangeUserInfoActivity.class);
			startActivity(intent);
		} else if ( position == 1 ) {
			Intent intent = new Intent(getActivity(), FriendListActivity.class);
			startActivity(intent);
		} else if ( position == 2 ) {
			Intent intent = new Intent(getActivity(), WeatherCitiesSettingActivity.class);
			startActivity(intent);
		} else if ( position == 3 ) {
			Intent intent = new Intent(getActivity(), UserCommunicateActivity.class);
			startActivity(intent);
		} else if ( position == 4 ) {
			setUserInfoDefaultToQuit();
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
		}

		super.onListItemClick(l, v, position, id);
	}
	
	public void getUserInfo() {
		SharedPreferences appInfo = getActivity().getSharedPreferences(Const.APP_INFO_PREFERENCE,  Context.MODE_MULTI_PROCESS);
		userName = appInfo.getString(Const.USER_NAME, "未登录");
		avatarUrl = appInfo.getString(Const.USER_AVATAR_URL, "");
		appInfo = null;
	}

	public void setUserInfoDefaultToQuit() {
		SharedPreferences appInfo = getActivity().getSharedPreferences(Const.APP_INFO_PREFERENCE, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = appInfo.edit();
		editor.putBoolean(Const.USER_LOG_IN_OR_NOT, false);
		editor.putBoolean(Const.USER_SIGN_IN_OR_NOT, false);
		editor.putString(Const.USER_AVATAR_URL, "");
		editor.putString(Const.USER_NAME, "未登录");
		editor.putString(Const.USER_AVATAR_URL, "");
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
			if ( position != 0 ) {
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
				Bitmap avatarBM = bitmapCache.getBitmapFromCache( avatarUrl );
				if ( avatarBM != null ) {
					BitmapDrawable avatarDrawable = new BitmapDrawable(getActivity().getApplicationContext().getResources(), avatarBM);
					icon.setImageDrawable(avatarDrawable);
				} else {
					bitmapCache.getImageFromNet(avatarUrl, "100-" + avatarUrl, icon.getHeight(), icon.getWidth(), icon);
				}
				icon.setImageResource(R.drawable.default_avatar);
				TextView title = (TextView) convertView
						.findViewById(R.id.user_name);
				title.setText(userName);
			}
			return convertView;
		}
	}
}
