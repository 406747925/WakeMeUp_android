package cn.jlu.ge.dreamclock.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.TextView;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.BitmapCache;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.FriendsDBAdapter;
import cn.jlu.ge.dreamclock.tools.MenuFragment;
import cn.jlu.ge.knightView.CircleImageView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class UserInfoActivity extends BaseActivity {

	private BitmapCache bitmapCache;
	private AsyncHttpClient client;
	private String userName;
	private String uid;
	private String avatarUrl;
	
	public UserInfoActivity() {
		super("用户信息");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		bitmapCache = new BitmapCache( getApplicationContext() );
		
		intentDataInit();
		
		init();
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// 设置 ActionBar 的背景色
		this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_main));
		super.onResume();
	}

	public void intentDataInit () {
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		userName = bundle.getString("userName", "error");
		uid = bundle.getString("uid", "-1");
		avatarUrl = bundle.getString("avatarUrl", "default");
		
	}
	
	
	public void init () {
        
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setContentView(R.layout.activity_user_info);
		
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

		setDatasAndViewsFromNet(uid);
		
		// TODO 如果没有联网则显示曾经数据
		setDatasFromDB();
		
		
		
	}
	

	public void setDatasFromDB () {
		FriendsDBAdapter friendsDb = new FriendsDBAdapter(getApplicationContext());
		
		friendsDb.close();
		
//		setUserAvatar ( avatarUrl );
	}
	
	public void setDatasAndViewsFromNet ( String UID ) {
		
		// 刷新用户数据
		
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
		
		
	void setUserInfoFromJSON (JSONObject userInfoObject) {
		try {
			String userName = userInfoObject.getString("nickname");
			String UIDStr = userInfoObject.getString("id");
			String collegeStr = userInfoObject.optString("college");
			String realNameStr = userInfoObject.optString("realname");
			String schoolStr = userInfoObject.optString("school");
			String avatarUrl = userInfoObject.optString("pic_url");
			String signInTimeStr = userInfoObject.optString("get_up_time_today");
			String genderStr = userInfoObject.optString("gender");
			
			int rankInFriendsNum = userInfoObject.optInt("rank_in_friends_today", 0);
			int rankInSchoolNum = userInfoObject.optInt("rank_in_school_today", 0);
			int rankInCollegeNum = userInfoObject.optInt("rank_in_college_today", 0);
			int continuousDaysSum = userInfoObject.optInt("continuous", 0);
			int jeerNum = userInfoObject.optInt("num_jeer_today", 0);
			int scoreNum = userInfoObject.optInt("score", 0);
			
			setUserInfoInDb( userName, UIDStr, collegeStr, realNameStr, avatarUrl, signInTimeStr, schoolStr, genderStr,
					rankInFriendsNum, rankInSchoolNum, scoreNum, rankInCollegeNum, continuousDaysSum, jeerNum );
			
			setUserInfoView( userName, collegeStr, signInTimeStr, schoolStr, genderStr,
					rankInSchoolNum, continuousDaysSum );
			
			setUserAvatar ( avatarUrl );
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void setUserAvatar ( String avatarUrl ) {
		CircleImageView avatarIv = (CircleImageView) findViewById(R.id.userAvatar);
		Bitmap avatarBM = bitmapCache.getBitmapFromCache( avatarUrl );
		if ( avatarBM != null ) {
			
			BitmapDrawable avatarDrawable = new BitmapDrawable(getApplicationContext().getResources(), avatarBM);
			avatarIv.setImageDrawable(avatarDrawable);
			
		}
		else {
			bitmapCache.getImageFromNet(avatarUrl, "100-" + avatarUrl, 100, 100, avatarIv);
		}
	}

	private void setUserInfoView(String userName, String collegeStr, 
			String signInTimeStr, String schoolStr, String genderStr, 
			int rankInSchoolNum, int continuousDaysSum ) {
		// TODO Auto-generated method stub
		TextView userNameTv = (TextView) findViewById(R.id.userName);
		TextView collegeTv = (TextView) findViewById(R.id.college);
		TextView signInTimeTv = (TextView) findViewById(R.id.recentGetUpTime);
		TextView schoolTv = (TextView) findViewById(R.id.school);
		TextView genderTv = (TextView) findViewById(R.id.gender);
		TextView rankInSchoolTv = (TextView) findViewById(R.id.recentRank);
		TextView continuousDaysTv = (TextView) findViewById(R.id.continueDay);
		
		userNameTv.setText(userName);
		schoolTv.setText(schoolStr);
		
		if ( genderStr != null ) {
			if ( genderStr.equals("m") )
				genderTv.setText("男的");
			else 
				genderTv.setText("女的");
		}
		
		if ( rankInSchoolNum != 0 )
			rankInSchoolTv.setText("第 " + rankInSchoolNum + " 名");
		else 
			rankInSchoolTv.setText("最近起不来");
		
		if ( continuousDaysSum != 0 )
			continuousDaysTv.setText("共 " + continuousDaysSum + " 天");
		else 
			continuousDaysTv.setText("名落孙山");
		
		if ( collegeStr != null )
			collegeTv.setText(collegeStr);
		else
			collegeTv.setText("未填写学院");
		
		if ( signInTimeStr != null ) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
				Date date = sdf.parse(signInTimeStr);
				sdf = new SimpleDateFormat("M月d日 h点m分", Locale.CHINA);
				signInTimeStr = sdf.format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			signInTimeTv.setText(signInTimeStr);
		}
		else 
			signInTimeTv.setText("最近比较懒");
	}

	private void setUserInfoInDb(String userName, String uIDStr,
			String collegeStr, String realNameStr, String avatarUrl,
			String signInTimeStr, String schoolStr, String genderStr, 
			int rankInFriendsNum, int rankInSchoolNum, int scoreNum, 
			int rankInCollegeNum, int continuousDaysSum, int jeerNum) {
		// TODO Auto-generated method stub
		
	}
	
	
}
