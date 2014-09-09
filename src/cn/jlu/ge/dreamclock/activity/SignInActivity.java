package cn.jlu.ge.dreamclock.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.BitmapCache;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.MenuFragment;
import cn.jlu.ge.dreamclock.tools.UserDataDBAdapter;
import cn.jlu.ge.knightView.CircleImageView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SignInActivity extends BaseActivity {

	public SignInActivity() {
		super("早起签到");
	}
	
	final String TAG = "SignInActivity";
	private int mySignInRank;
	private int signInUsersNum;
	private ListView usersList;
	private AsyncHttpClient client;
	private SharedPreferences appInfo;
	private Bitmap zoomAvatar;
	private ArrayList< HashMap<String, Object> > signInUsersList;
	private BitmapCache bitmapCache;
	private int invisableLayoutHeight = 0;
	private int normalLayoutHeight;
	String UIDStr = "123456789";
	String timeStr = "2014-09-08%2004:00:00";
	String myUserNameStr = null;
	String mySignInTimeStr = null;
	String myAvatarUrl = null;
	private UserDataDBAdapter userDataDb;
	boolean isGoingToSignInBool = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		bitmapCache = new BitmapCache(getApplicationContext());
		userDataDb = new UserDataDBAdapter(getApplicationContext());
		signInUsersList = new ArrayList< HashMap<String, Object> > ();
		init();
	}
	
//	ViewTreeObserver.OnGlobalLayoutListener usersListLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
//
//		@Override
//		public void onGlobalLayout() {
//			// TODO Auto-generated method stub
//			int usersListHeight = usersList.getHeight();
//			ViewGroup.LayoutParams params = usersList.getLayoutParams();
//			params.height = usersListHeight + invisableLayoutHeight;
//			Log.d(TAG, "usersListHeight: " + usersListHeight + ", invisableLayoutHeight: " + invisableLayoutHeight);
//			
//		}
//		
//	};
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// 设置 ActionBar 的背景色
		this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_main));
		super.onResume();
	}

	public void init() {
        
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		setContentView(R.layout.activity_sign_in);
		
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
		dataAndViewInit();
		signInUsersRankViewInit();
	}
	
	void signInUsersRankViewInit () {
		TextView signInRankTv = (TextView) findViewById(R.id.signInRank);
		signInRankTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ViewGroup parent = (ViewGroup)v.getParent().getParent();
				View userLayout = (View)parent.findViewById(R.id.userLayout);
				if ( userLayout.getVisibility() == View.VISIBLE )
					userLayout.setVisibility(View.GONE);
				else 
					userLayout.setVisibility(View.VISIBLE);
			}
			
		});
	}
	
	public void dataAndViewInit () {
		// TODO 如果数据库有最新数据则从数据库中获取最新数据
		
		setUserSignInViewsWithCache();
		// TODO 数据库中不包含最新数据则联网更新数据
		
	}
	
	void setUserSignInViewsWithCache () {
		appInfo = getSharedPreferences( Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS );
		String userName = appInfo.getString(Const.USER_NAME, "Me");
		String UID = appInfo.getString(Const.USER_ID, "123456789");
		int continuousDaysSum = appInfo.getInt(Const.USER_CONTINUOUS_SIGN_IN_DAYS, 0);
		int jeerNum = appInfo.getInt(Const.USER_BEEN_JEER_NUM, 0);
		int scoreNum = appInfo.getInt(Const.USER_SCORE, 0);
		int rankNum = appInfo.getInt(Const.USER_RANK, 1);
		String avatarUrl = appInfo.getString(Const.USER_AVATAR_URL, "");
		String signInTimeStr = appInfo.getString(Const.USER_SIGN_IN_TIME, "未更新");
		String getUsersListLastTimeStr = appInfo.getString(Const.GET_USERS_LIST_LAST_TIME , "");
		int signInUsersSum = appInfo.getInt(Const.SIGN_IN_RANK_NUM, 1);
		boolean signInOrNot = appInfo.getBoolean(Const.USER_SIGN_IN_OR_NOT, false);
//		boolean signInOrNot = false;
		appInfo = null;

		myAvatarUrl = avatarUrl;
		mySignInRank = rankNum;
		myUserNameStr = userName;
		mySignInTimeStr = signInTimeStr;
		signInUsersNum = signInUsersSum;
		timeStr = getUsersListLastTimeStr;
		UIDStr = UID;
		
		if ( signInOrNot ) {
			setUserSignInViews(userName, continuousDaysSum, jeerNum, scoreNum, rankNum);
			getSignInUsersListFromNet( UIDStr, timeStr );
			// 已经签到过，就先从数据库中获得数据
			getUsersInfromFromDB();
		} else {
			
			Button signInBtn = (Button) findViewById(R.id.signInBtn);
			signInBtn.setVisibility(View.VISIBLE);
			signInBtn.setOnClickListener(signInBtnOnClickListener);
			TextView userNameTv = (TextView) findViewById(R.id.userName);
			userNameTv.setText(userName);
			TextView continuousDaysTv = (TextView) findViewById(R.id.signInDays);
			TextView jeerNumTv = (TextView) findViewById(R.id.shame);
			TextView scoreTv = (TextView) findViewById(R.id.score);
			TextView rankTv = (TextView) findViewById(R.id.rank);
			continuousDaysTv.setVisibility(View.GONE);
			jeerNumTv.setVisibility(View.GONE);
			scoreTv.setVisibility(View.GONE);
			rankTv.setVisibility(View.GONE);
			
		}
		
		setUserAvatar ( avatarUrl );
		
	}
	
	OnClickListener signInBtnOnClickListener = new OnClickListener () {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if ( !isGoingToSignInBool ) {
				userDoSignIn ( UIDStr );
				Toast.makeText(getApplicationContext(), "正在前往签到处～", Toast.LENGTH_SHORT).show();				
			} else {
				Toast.makeText(getApplicationContext(), "正在前往签到处，别心急咯～", Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	void setUserSignInViews (String userName, int continuousDaysSum, int jeerNum, int scoreNum, int rankNum) {
		
		Button signInBtn = (Button) findViewById(R.id.signInBtn);
		signInBtn.setVisibility(View.GONE);
		
		TextView userNameTv = (TextView) findViewById(R.id.userName);
		TextView continuousDaysTv = (TextView) findViewById(R.id.signInDays);
		TextView jeerNumTv = (TextView) findViewById(R.id.shame);
		TextView scoreTv = (TextView) findViewById(R.id.score);
		TextView rankTv = (TextView) findViewById(R.id.rank);
		
		continuousDaysTv.setVisibility(View.VISIBLE);
		jeerNumTv.setVisibility(View.VISIBLE);
		scoreTv.setVisibility(View.VISIBLE);
		rankTv.setVisibility(View.VISIBLE);
		
		userNameTv.setText(userName);
		rankTv.setText( getRankNumDescStr( rankNum ) );
		continuousDaysTv.setText( getContinuousDaysDescStr( continuousDaysSum ) );
		jeerNumTv.setText( getJeerNumDescStr( jeerNum ) );
		scoreTv.setText( getScoreDescStr( scoreNum ) );
		
	}

	void setUserSignInInfo (String userName, int continuousDaysSum, 
				int jeerNum, int scoreNum, int rankNum, 
				String picUrl, String signInTimeStr, String UIDStr,
				String getUsersListLastTimeStr) {
	
		appInfo = getSharedPreferences( Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS );
		SharedPreferences.Editor editor = appInfo.edit();
		editor.putString(Const.USER_NAME, userName);
		editor.putInt(Const.USER_CONTINUOUS_SIGN_IN_DAYS, continuousDaysSum);
		editor.putInt(Const.USER_BEEN_JEER_NUM, jeerNum);
		editor.putInt(Const.USER_SCORE, scoreNum);
		editor.putInt(Const.USER_RANK, rankNum);
		editor.putString(Const.USER_ID, UIDStr);
		editor.putString(Const.USER_SIGN_IN_TIME, signInTimeStr);
		editor.putString(Const.USER_AVATAR_URL, picUrl);
		editor.putBoolean(Const.USER_SIGN_IN_OR_NOT, true);
		if ( getUsersListLastTimeStr != null )
			editor.putString(Const.GET_USERS_LIST_LAST_TIME, getUsersListLastTimeStr);
		editor.putInt(Const.SIGN_IN_RANK_NUM, 1);
		
		editor.commit();
		
		mySignInRank = rankNum;
		myUserNameStr = signInTimeStr;
		myUserNameStr = userName;
		mySignInTimeStr = signInTimeStr;
		if ( getUsersListLastTimeStr != null )
			timeStr = getUsersListLastTimeStr;
		
	}
	
	
	// 获取和设置用户头像
	
	void setUserAvatar ( String avatarUrl ) {
		CircleImageView avatarIv = (CircleImageView) findViewById(R.id.userAvatar);
		Bitmap avatarBM = bitmapCache.getBitmapFromCache(avatarUrl);
		if ( avatarBM != null ) {
			
			BitmapDrawable avatarDrawable = new BitmapDrawable(getApplicationContext().getResources(), avatarBM);
			avatarIv.setImageDrawable(avatarDrawable);
			
		}
		else {
			bitmapCache.getImageFromNet(avatarUrl, "100-" + avatarUrl, 100, 100, avatarIv);
		}
	}
	
	
//	格式化用户签到信息，
//	包括今日签到名次，
//	连续签到天数，
//	收到嘲笑数目，
//	获得积分数目
	
	String getRankNumDescStr( int rankNum ) {
		if ( rankNum < 1 ) return "名次信息出错了-，-";
		
		// TODO 是否有针对不同排名的个性化文字需求
		return "今天签到排名 " + rankNum + " 名";
		
	}
	
	String getContinuousDaysDescStr( int continuousDaysSum ){
		if ( continuousDaysSum < 1 )	return "一次连续签到都木有！";
		// TODO 是否有针对不同排名的个性化文字需求
		return "目前连续签到 " + continuousDaysSum + " 天";
	}
	
	String getJeerNumDescStr( int jeerNum ) {
		if ( jeerNum < 1 )	return "还没有人嘲笑你哦～";
		// TODO 是否有针对不同排名的个性化文字需求
		return "今天收到嘲笑 " + jeerNum + " 次";
	}
	
	String getScoreDescStr( int scoreNum ) {
		if ( scoreNum < 1 ) 	return "分数为负，请多努力！";
		// TODO 是否有针对不同排名的个性化文字需求
		return "一共获得积分 " + scoreNum + " 分";
	}
	
	String setSignInScoreByTime() {
		
		return "2";
		
	}
	
	//
	
	void setUserInfoFromJSON (JSONObject userInfoObject) {
		try {
			String userName = userInfoObject.get("nickname").toString();
			int continuousDaysSum = userInfoObject.getInt("continuous");
			int jeerNum = userInfoObject.getInt("num_jeer_today");
			int scoreNum = userInfoObject.getInt("score");
			String UIDStr = userInfoObject.getString("id");
			int rankNum = userInfoObject.getInt("rank_in_friends_today");
			String avatarUrl = userInfoObject.getString("pic_url");
			String signInTimeStr = userInfoObject.getString("get_up_time_today");
			myAvatarUrl = avatarUrl;
			mySignInRank = rankNum;
			
			String getUsersListLastTimeStr = null;
			
			setUserSignInInfo(userName, continuousDaysSum, jeerNum, scoreNum,
						rankNum, avatarUrl, signInTimeStr, UIDStr, getUsersListLastTimeStr);
			setUserSignInViews(userName, continuousDaysSum, jeerNum, scoreNum, rankNum);
			
			setUserAvatar( avatarUrl);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// 刷新用户数据
	
	void getUserInfoFromNet ( String UID ) {
		client = new AsyncHttpClient();
		
		String userInfoUrl = Const.HOST + Const.GET_USER_INFO_URL + UID;
		
		client.get( userInfoUrl, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable throwable, String failResponse) {
				// TODO Auto-generated method stub
				super.onFailure(throwable, failResponse);
			}

			@Override
			public void onSuccess(int responseCode, String response) {
				// TODO Auto-generated method stub
				try {
					JSONObject responseObject = new JSONObject(response);
					setUserInfoFromJSON ( responseObject.getJSONObject("model") );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(responseCode, response);
			}
			
		});
		
		client = null;
	}
	
	
	// 通过网络签到，获取用户签到信息
	
	void userDoSignIn ( final String UID ) {
		
		client = new AsyncHttpClient();
		String userSignInInfoUrl = Const.HOST + Const.USER_SIGN_IN_URL;
		userSignInInfoUrl = String.format(userSignInInfoUrl, UID, 2);
		
		Log.d(TAG, userSignInInfoUrl);
		
		client.get( userSignInInfoUrl , new AsyncHttpResponseHandler () {

			@Override
			public void onFailure(Throwable throwable, String failResponse) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "签到失败，请检查网络！～", Toast.LENGTH_SHORT).show();
				super.onFailure(throwable, failResponse);
			}

			@Override
			public void onSuccess(int responseCode, String responseStr) {
				// TODO Auto-generated method stub
				Log.v("UserInfo" , "" + responseStr);
				try {
					JSONObject responseObject = new JSONObject(responseStr);
					int statusCode = responseObject.getInt("statusCode");
					if ( statusCode == 200 ) {
						Toast.makeText(getApplicationContext(), "恭喜签到成功！～", Toast.LENGTH_SHORT).show();
						signInSuccessAndUpdateViews (responseObject);
					} else if ( statusCode == 301 ) {
						Toast.makeText(getApplicationContext(), "今天已经签到过啦～", Toast.LENGTH_SHORT).show();
						getUserInfoFromNet ( UID );
						getSignInUsersListFromNet ( UID , timeStr);
					}
					else {
						Toast.makeText(getApplicationContext(), "签到失败，屌丝不哭！～", Toast.LENGTH_SHORT).show();
						signInFailedAndUpdateViews (responseObject);
						
					}
					
					isGoingToSignInBool = true;
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(responseCode, responseStr);
			}
			
		});
		
		client = null;
	}
	
	void signInSuccessAndUpdateViews (JSONObject responseObject) {
		try {
			JSONObject userInfoObject = responseObject.getJSONObject("user");
			String userName = userInfoObject.get("nickname").toString();
			int continuousDaysSum = userInfoObject.getInt("continuous");
			int jeerNum = userInfoObject.getInt("num_jeer_today");
			int scoreNum = userInfoObject.getInt("score");
			String UIDStr = userInfoObject.getString("id");
			int rankNum = userInfoObject.getInt("rank_in_friends_today");
			
			String getUsersListLastTimeStr = responseObject.getString("time");
			String signInTimeStr = getUsersListLastTimeStr;
			String avatarUrl = userInfoObject.getString("pic_url");
			myAvatarUrl = avatarUrl;
			mySignInRank = rankNum;
			
			setUserSignInInfo(userName, continuousDaysSum, jeerNum, scoreNum,
						rankNum, avatarUrl, signInTimeStr, UIDStr, getUsersListLastTimeStr);
			setUserSignInViews(userName, continuousDaysSum, jeerNum, scoreNum, rankNum);
			
			setUserAvatar( avatarUrl);
			
			getSignInUsersListFromNet ( UIDStr, timeStr);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	void signInFailedAndUpdateViews (JSONObject responseObject) {

		
		
	}
	
	boolean getUsersInfromFromDB () {
		userDataDb.open();
		
		Cursor cursor = userDataDb.getAllUsers();
		
		if ( cursor == null || cursor.moveToFirst() == false )
			return false;
		
		if ( signInUsersList == null ) {
			signInUsersList = new ArrayList< HashMap <String, Object> > ();
		}
		
		int nickNameColumn = cursor.getColumnIndex(UserDataDBAdapter.KEY_NICKNAME);
		int avatarUrlColumn = cursor.getColumnIndex(UserDataDBAdapter.KEY_USER_AVATAR_URL);
		int rankColumn = cursor.getColumnIndex(UserDataDBAdapter.KEY_USER_RANK);
		int uidColumn = cursor.getColumnIndex(UserDataDBAdapter.KEY_USER_ID);
		int jeerOrNotColumn = cursor.getColumnIndex(UserDataDBAdapter.KEY_USER_JEER_OR_NOT);
		int signInTimeColumn = cursor.getColumnIndex(UserDataDBAdapter.KEY_USER_SIGN_IN_TIME);
		int contentStrColumn = cursor.getColumnIndex(UserDataDBAdapter.KEY_USER_INFO);
		
		int rank = 0;
		String userName = null;
		String uidStr = null;
		String userSignInTimeStr = null;
		String contentStr = null;
		int jeerOrNot = 0;
		String avatarUrl = null;
		for ( cursor.moveToFirst() ; !cursor.isLast() ; cursor.moveToNext() ) {
			userName = cursor.getString(nickNameColumn);
			rank = cursor.getInt(rankColumn);
			uidStr = cursor.getString(uidColumn);
			userSignInTimeStr = cursor.getString(signInTimeColumn);
			contentStr = cursor.getString(contentStrColumn);
			jeerOrNot = Integer.parseInt( cursor.getString(jeerOrNotColumn) );
			avatarUrl = cursor.getString(avatarUrlColumn);
			addItemToList (rank, userName, uidStr, userSignInTimeStr, contentStr, jeerOrNot, avatarUrl );
		}
		
		userName = cursor.getString(nickNameColumn);
		rank = cursor.getInt(rankColumn);
		uidStr = cursor.getString(uidColumn);
		userSignInTimeStr = cursor.getString(signInTimeColumn);
		contentStr = cursor.getString(contentStrColumn);
		jeerOrNot = Integer.parseInt( cursor.getString(jeerOrNotColumn) );
		avatarUrl = cursor.getString(avatarUrlColumn);
		
		addItemToList (rank, userName, uidStr, userSignInTimeStr, contentStr, jeerOrNot, avatarUrl );
		
		cursor.close();
		userDataDb.close();
		
		return true;
	}
	
	
	
	// 获取用户好友的签到信息
	
	void getSignInUsersListFromNet ( String UID , String timeStr ) {
		
		client = new AsyncHttpClient();
		timeStr = timeStr.replaceAll(" ", "%20");
		String signInUsersListUrl = String.format( Const.HOST + Const.GET_USERS_SIGN_IN_LIST_URL, UID, timeStr );
		Log.d(TAG, signInUsersListUrl);
		client.get( signInUsersListUrl , new AsyncHttpResponseHandler () {

			@Override
			public void onFailure(Throwable throwable, String failResponse) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "刷新签到列表失败 T-T", Toast.LENGTH_SHORT).show();

//				String responseStr = "[{'nickname':'1234', 'friend_id': '1234', 'get_up_time_today': '2014-09-08 04:00:00', " +
//						"'content': '我勒个去我勒个去我勒个去我勒个去', 'pic_url': '', 'jeerOrNot': '0'}," +
//						"{'nickname':'1235', 'friend_id': '1235', 'get_up_time_today': '2014-09-08 05:00:00', " +
//						"'content': '我勒个去我勒个去我勒个去我勒个去', 'pic_url': '', 'jeerOrNot': '0'}]";
//				responseStr = responseStr.replaceAll("'", "\"");
//				String timeStr = "2014-09-08 05:00:00";
//				try {
//					JSONArray usersListArray = new JSONArray(responseStr);
//					int rankStart = signInUsersNum;
//					setUsersListDataFromJSON( usersListArray );
//					signInUsersNum += usersListArray.length();
					setSignInUsersView();
//					appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
//					SharedPreferences.Editor editor = appInfo.edit();
//					editor.putString(Const.GET_USERS_LIST_LAST_TIME, timeStr);
//					Log.d( TAG, ">>>> " + timeStr);
//					editor.putInt(Const.SIGN_IN_RANK_NUM, signInUsersNum);
//					editor.commit();
//					addUsersToDB(rankStart, signInUsersNum);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				super.onFailure(throwable, failResponse);
			}

			@Override
			public void onSuccess(int responseCode, String response) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "签到列表获取成功", Toast.LENGTH_SHORT).show();
				try {
					JSONObject signInUsersInfoObject = new JSONObject( response );
					String timeStr = signInUsersInfoObject.getString("time");
					JSONArray usersListArray = signInUsersInfoObject.getJSONArray("list");
					Log.v("SignInActivity", usersListArray.toString());
					int rankStart = signInUsersNum;
					setUsersListDataFromJSON( usersListArray );
					signInUsersNum += usersListArray.length();
					setSignInUsersView();
					appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
					SharedPreferences.Editor editor = appInfo.edit();
					editor.putString(Const.GET_USERS_LIST_LAST_TIME, timeStr);
					Log.d( TAG, ">>>> " + timeStr);
					editor.putInt(Const.SIGN_IN_RANK_NUM, signInUsersNum);
					editor.commit();
					addUsersToDB(rankStart, signInUsersNum);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(responseCode, response);
			}
			
		});
		
		client = null;
		
	}
	
	
	// 将追加的签到的好友信息储存至数据库中
	void addUsersToDB (int rankStart, int signInUsersNum) {
		
		if ( rankStart == signInUsersNum )
			return ;
		
		HashMap<String, Object>userHM;
		String userName = null, 
				uid = null, 
				signInTime = null,
				contentStr = null,
				jeerOrNot = null,
				avatarUrl = null;
		
		userDataDb.open();
		
		signInUsersNum = signInUsersNum < signInUsersList.size() ? signInUsersNum : signInUsersList.size();
		
		for ( int i = rankStart - 1 ; i < signInUsersNum - 1 ; i++ ) {
			
			userHM = signInUsersList.get(i);
			int userRank = Integer.parseInt( userHM.get("userRank").toString() );
			userName = userHM.get("userName").toString();
			uid = userHM.get("uid").toString();
			signInTime = userHM.get("time").toString();
			contentStr = userHM.get("info").toString();
			jeerOrNot = userHM.get("jeerOrNot").toString();
			avatarUrl = userHM.get("avatarUrl").toString();
			
			long id = userDataDb.insertOrUpdateUser(uid, signInTime, userName, jeerOrNot, contentStr, avatarUrl, userRank);
			if ( id < -1 )
				Log.v(TAG, "num : " + i + ", insert error.");
		}
		
		userDataDb.close();

	}

	
	// 将一个好友签到的信息加入到一个 ArrayList 全局变量中
	
	void addItemToList ( int rank, String userName, String friendId, String getUpTimeStr, String contentStr, int jeerOrNot, String avatarUrl ) {
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("userRank", rank);
		map.put("userName", userName);
		map.put("uid", friendId);
		map.put("time", getUpTimeStr);
		map.put("info", contentStr);
		map.put("jeerOrNot", jeerOrNot);
		map.put("avatarUrl", avatarUrl);
		
		signInUsersList.add(map);
		
	}
	
	
	// 将用户个人签到信息加入到签到列表中，一个全局的 ArrayList
	
	void addUserInAllUsersList () {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("userRank", mySignInRank);
		map.put("userName", myUserNameStr);
		map.put("uid", UIDStr);
		map.put("time", mySignInTimeStr.split(" ")[1].substring(0, 5));
		map.put("info", "");
		map.put("jeerOrNot", "-1");
		map.put("avatarUrl", myAvatarUrl);
		
		signInUsersList.add(map);
	}
	
	
	// 将从网络上获取的用户好友签到信息由 JSON 转换为 ArrayList
	
	void setUsersListDataFromJSON ( JSONArray array ) {
		
		if ( signInUsersList == null )
			signInUsersList = new ArrayList< HashMap<String, Object> > ();

		int length = array.length();
		try {
			JSONObject object;
			for ( int i = 0, rank = 1 ; i < length ; i++ ) {
				
				object = array.getJSONObject(i);
				String userName = object.getString("nickname");
				String friendId = object.getString("friend_id");
				String getUpTimeStr = object.getString("get_up_time_today");
				String contentStr = object.getString("content");
				String avatarUrl = object.getString("pic_url");
				int jeerOrNot = object.getInt("type");
				// TODO 需要安全性更高的时间格式化方法
				getUpTimeStr = getUpTimeStr.split(" ")[1].substring(0, 5);
				
				Log.d(TAG, ">>> i : " + i + ", mySignInRank : " + mySignInRank);
				
				if ( i == mySignInRank - 1 ) {
					addUserInAllUsersList();
					rank = rank + 1;
				}
				
				addItemToList( rank, userName, friendId, getUpTimeStr, contentStr, jeerOrNot, avatarUrl );
				rank = rank + 1;
				
			}
			
			if ( length == ( mySignInRank - 1 ) && mySignInRank >= signInUsersList.size() )
				addUserInAllUsersList();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	// 设置一个用户的头像，通过用户头像的链接，从内存和本地文件夹中寻找，
	// 如果内存中不存在，则访问本地文件夹，否则访问网络下载到本地中
	
	public void setUserAvatar ( String avatarUrl, final int width, final int height, final CircleImageView avatarView ) {
		if ( bitmapCache == null )
			return ;
		Bitmap avatarBM = bitmapCache.getBitmapFromCache( avatarUrl );
		if ( avatarBM != null ) {
			
			BitmapDrawable avatarDrawable = new BitmapDrawable( getApplicationContext().getResources(), avatarBM );
			avatarView.setImageDrawable(avatarDrawable);
//			avatarView.setOnClickListener();
		}
		else {
			bitmapCache.getImageFromNet ( avatarUrl, avatarUrl, width, height, avatarView );
		}
	}
	
	
	// 头像点击事件
//	OnClickListener 
	
	
	// 加载用户好友们的签到信息 
	
	public void setSignInUsersView () {
		// TODO 加载数据显示 View 
        usersList = (ListView) findViewById(R.id.signInUserList);
//        usersList.getViewTreeObserver().addOnGlobalLayoutListener(usersListLayoutListener);
        UsersSignInAdapter listAdapter = new UsersSignInAdapter(this);
        usersList.setAdapter(listAdapter);
        normalLayoutHeight = usersList.getHeight();
	}
	
	
	// 用户签到列表适配器
	
	class UsersSignInAdapter extends BaseAdapter {

		LayoutInflater inflater;
	    public Context context;
	    public UsersSignInAdapter (Context c) {
	    	context = c;
	    	inflater = LayoutInflater.from(c);
	    }
	    
		class ListClickGroup {  
		    public CircleImageView avatarIV;
		    public TextView usernameTV;
		    public TextView rankTV;
		    public TextView timeTV;
		    public TextView infoTV;
		    public Button showInfoBtn;
		    public int uid;
		    public int position;
		    public boolean goneOrNot;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return signInUsersList.size();
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
				convertView = inflater.inflate(R.layout.earlier_user_item, null);
				clickViews.avatarIV = (CircleImageView) convertView.findViewById(R.id.avatar);
				clickViews.usernameTV = (TextView) convertView.findViewById(R.id.userName);
				clickViews.rankTV = (TextView) convertView.findViewById(R.id.userRank);
				clickViews.infoTV = (TextView) convertView.findViewById(R.id.info);
				clickViews.timeTV = (TextView) convertView.findViewById(R.id.time);
				clickViews.showInfoBtn = (Button) convertView.findViewById(R.id.showInfo);
				convertView.setTag(clickViews);
			}
			
			clickViews.position = position;
			Log.v("SignInActivity", "positon : " + position);
			
			if ( position < signInUsersNum ) {
				
				clickViews.usernameTV.setText( signInUsersList.get(position).get("userName").toString() );
				clickViews.rankTV.setText( signInUsersList.get(position).get("userRank").toString() );
				clickViews.infoTV.setText( signInUsersList.get(position).get("info").toString() );
				clickViews.timeTV.setText( signInUsersList.get(position).get("time").toString() );
				LinearLayout infoLayout = (LinearLayout) clickViews.infoTV.getParent();
				
//				ViewTreeObserver vto = infoLayout.getViewTreeObserver();
//				vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//					
//					@Override
//					public boolean onPreDraw() {
//						// TODO Auto-generated method stub
//						int height = infoLayout.getMeasuredHeight();
//						int width = infoLayout.getMeasuredWidth();
//						return true;
//					}
//					
//				});
				
				if ( infoLayout.getVisibility() == View.GONE ) {
					invisableLayoutHeight = infoLayout.getHeight();
					Log.d(TAG, "invisableLayoutHeight: " + invisableLayoutHeight);
				}
				
				String jeerOrNot = signInUsersList.get(position).get("jeerOrNot").toString();
				if ( jeerOrNot.equals("-1") ) {
					clickViews.showInfoBtn.setVisibility(View.INVISIBLE);
				} else if ( jeerOrNot.equals("0") ) {
					clickViews.showInfoBtn.setVisibility(View.VISIBLE);
				}
				
				clickViews.showInfoBtn.setOnClickListener(showJeerClickListener);
				
			} else {
				
				clickViews.rankTV.setText("-");
				clickViews.timeTV.setText("未起床");
				clickViews.usernameTV.setText( signInUsersList.get(position).get("userName").toString() );
				
			}
			
			String avatarUrl = signInUsersList.get(position).get("avatarUrl").toString();
			setUserAvatar ( avatarUrl, 45, 45, clickViews.avatarIV );
			
			return convertView;
		}
		
	}
	
	
	
	

	OnClickListener showJeerClickListener = new OnClickListener () {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			LinearLayout layout = (LinearLayout) view.getParent().getParent();
			TextView infoTv = (TextView) layout.findViewById(R.id.info);
			View jeerContentLayout = (View) layout.findViewById(R.id.jeerContent);
			int height;
			if ( jeerContentLayout.getVisibility() == View.GONE ) {
				(( Button ) view).setText("收起");
				view.setBackgroundResource(R.drawable.unclick_button);
				jeerContentLayout.setVisibility(View.VISIBLE);
//				normalLayoutHeight = usersList.getHeight();
//				invisableLayoutHeight = ( (ViewGroup)infoTv.getParent() ).getMeasuredHeight();
//				invisableLayoutHeight = ( invisableLayoutHeight == 0 ) ? ( 3 * layout.getHeight()/5 ) : ( invisableLayoutHeight );
//				height = usersList.getHeight() + invisableLayoutHeight;
				Log.v(TAG, "infoTv view gone to visible. Height: " + invisableLayoutHeight);
			} else {
				(( Button ) view).setText("查看");
				view.setBackgroundResource(R.drawable.click_button);
//				invisableLayoutHeight = ( (ViewGroup)infoTv.getParent() ).getMeasuredHeight();
//				height = usersList.getHeight() - invisableLayoutHeight;
				jeerContentLayout.setVisibility(View.GONE);
				Log.v(TAG, "infoTv view visible to gone.Height: " + invisableLayoutHeight);
			}
			
		}
		
	};

	
	public void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null) {
			return;
		}
		
		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		listView.setLayoutParams(params);
	
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        
		switch(item.getItemId()) {
        
        case R.id.alarm_list:
        	Intent newIntent = new Intent(getApplicationContext(), AlarmListActivity.class);
        	startActivity(newIntent);
            break;
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
        }
		
		return super.onOptionsItemSelected(item);
		
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if ( zoomAvatar != null )
			zoomAvatar.recycle();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate( R.menu.sign_in, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
}
