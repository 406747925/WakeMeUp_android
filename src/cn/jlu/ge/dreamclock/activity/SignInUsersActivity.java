package cn.jlu.ge.dreamclock.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.MenuFragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SignInUsersActivity extends BaseActivity {

	private ArrayList<HashMap<String, Object>> listItems;
	private ListView usersList;
	
	public SignInUsersActivity() {
		super("积分排行");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}
	
	
	public void init() {
        
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setContentView(R.layout.activity_sign_in_users);
		
		viewInit();
		
		// 因为在父类 BaseActivity 的 onCreate 方法执行时， 所需要的 Fragment 控件必须是 子控件，
        // 方法中的 FragmentTransaction 会使用 id 资源 ( R.id.menu_frame2 ) 引用 Fragment 控件，
        // 如果未先将对应的 Fragment 控件设置为子控件进行初始化， FragmentManager 将会找不到这个子控件，
		// 而在绘制界面时才抛出运行时异常
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame2, new MenuFragment()).commit();
	}
	
	public void viewInit () {
		// TODO 如果已经联网，如果更新过数据则直接从数据库显示数据，否则联网更新数据
		dataInit();
		// TODO 如果没有联网则显示曾经数据
		setSignInUsersView();
	}
	
	public void dataInit () {
		// TODO 如果数据库有最新数据则从数据库中获取最新数据

		// TODO 数据库中不包含最新数据则联网更新数据
		
		// Test
		listItems = new ArrayList<HashMap<String, Object>> ();
		HashMap<String, Object> item;
		
		for ( int i = 0 ; i < 10 ; i++ ) {
			item = new HashMap<String, Object> ();
			item.put("username", "路人甲" + i);
			item.put("userRank", "" + i);
			listItems.add(item);
		}
		
	}
	
	public void setSignInUsersView () {
		// TODO 加载数据显示 View 
        usersList = (ListView) findViewById(R.id.signInUsers);
        UsersAdapter listAdapter = new UsersAdapter(this);
        usersList.setAdapter(listAdapter);
	}
	
	class UsersAdapter extends BaseAdapter {

		LayoutInflater inflater;
	    public Context context;
	    public UsersAdapter (Context c) {
	    	context = c;
	    	inflater = LayoutInflater.from(c);
	    }
	    
		class ListClickGroup {  
		    public ImageButton avatarIV;
		    public TextView usernameTV;
		    public TextView rankTV;
		    public int uid;
		    public int position;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ListClickGroup clickViews = null;
			if ( convertView != null ) {
				clickViews = (ListClickGroup) convertView.getTag();
				Log.v("tag", "positon " + position + " convertView is not null, "  + clickViews);
			} else {
				clickViews = new ListClickGroup();
				convertView = inflater.inflate(R.layout.user_item, null);
				clickViews.avatarIV = (ImageButton) convertView.findViewById(R.id.avatar);
				clickViews.usernameTV = (TextView) convertView.findViewById(R.id.userName);
				clickViews.rankTV = (TextView) convertView.findViewById(R.id.userRank);
			}
			
			clickViews.position = position;
			
			clickViews.avatarIV.setBackgroundResource(R.drawable.ic_launcher);
			clickViews.usernameTV.setText( listItems.get(position).get("username").toString() );
			clickViews.rankTV.setText( listItems.get(position).get("userRank").toString() );
			
			return convertView;
		}
		
	}
	
}
