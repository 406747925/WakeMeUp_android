package cn.jlu.ge.dreamclock.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.Const;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class ChangeUserInfoActivity extends SherlockActivity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setTitle("修改个人信息");
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_main));
		init();
		
	}
	
	public void init() {
		menuInit();
		dataInit();
		viewInit();
	}
	
	public void menuInit() {
		setContentView(R.layout.activity_change_user_info);
	}
	
	public void dataInit() {

	}
	
	public void viewInit() {
		SharedPreferences appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
		String userNameStr = appInfo.getString(Const.USER_NAME, "未更新");
		String collegeStr = appInfo.getString(Const.USER_COLLEGE, "未更新");
		String schoolStr = appInfo.getString(Const.USER_SCHOOL, "未更新");
		String genderStr = appInfo.getString(Const.USER_SEX, "还用说?");
		String signInTimeStr = appInfo.getString(Const.USER_SIGN_IN_TIME, "还没起来过？O_o");
		int continuousDaysSum = appInfo.getInt(Const.USER_CONTINUOUS_SIGN_IN_DAYS, -1);

		setUserChangableInfoView(userNameStr, collegeStr, schoolStr, genderStr, signInTimeStr, continuousDaysSum);
	}
	
	public void setUserChangableInfoView(String userNameStr, String collegeStr, String schoolStr, String genderStr, String signInTimeStr, int continuousDaysSum) {
		EditText userNameTv = (EditText) findViewById(R.id.userName);
		EditText collegeTv = (EditText) findViewById(R.id.college);
		EditText schoolTv = (EditText) findViewById(R.id.school);
		EditText genderTv = (EditText) findViewById(R.id.gender);
		
		TextView signInTimeTv = (TextView) findViewById(R.id.recentGetUpTime);
		TextView continuousDaysTv = (TextView) findViewById(R.id.continueDay);
		
		userNameTv.setText(userNameStr);
		schoolTv.setText(schoolStr);
		
		if ( genderStr != null ) {
			if ( genderStr.equals("m") )
				genderTv.setText("男的");
			else 
				genderTv.setText("女的");
		}
		
		if ( continuousDaysSum > 0 )
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.change_user_info, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch ( item.getItemId() ) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		case R.id.change_user_info:
			doChangeUserInfoToCache();
			doChangeUserInfoToNet();
		}
		return super.onOptionsItemSelected(item);
	}

	private void doChangeUserInfoToCache() {
		// TODO Auto-generated method stub
		
	}

	private void doChangeUserInfoToNet() {
		// TODO Auto-generated method stub
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "";
		client.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable throwable, String failResponse) {
				// TODO Auto-generated method stub
				super.onFailure(throwable, failResponse);
			}

			@Override
			public void onSuccess(int code, String response) {
				// TODO Auto-generated method stub
				super.onSuccess(code, response);
			}
			
		});
	}
	
	

}
