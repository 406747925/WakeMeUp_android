package cn.jlu.ge.dreamclock.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.ReadParseClass;

import com.actionbarsherlock.app.SherlockActivity;

public class LoginActivity extends SherlockActivity{
	String mphonenum;
	String mpasswd;
	String mpasswdorigen;
	SharedPreferences pre;
	ProgressDialog mDialog;
	private final String TAG = "LoginActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        pre = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);

        setContentView(R.layout.activity_login);
		getActionBar().hide();
		
		EditText phonenum=(EditText)findViewById(R.id.editTextphone);
		phonenum.setText(pre.getString(Const.USER_PHONE_NUMBER, null));
		EditText passwd=(EditText)findViewById(R.id.editTextpasswd);
		passwd.setText(pre.getString(Const.USER_PASSWD, null));
		View v=findViewById(R.id.textViewsignin);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(),SignUpActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
		});
		
		Button b=(Button)findViewById(R.id.buttonsignin);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText phonenum=(EditText)findViewById(R.id.editTextphone);
				mphonenum=phonenum.getText().toString();
				EditText passwd=(EditText)findViewById(R.id.editTextpasswd);
				mpasswd=passwd.getText().toString();
				mpasswdorigen = mpasswd;
				mpasswd=SignUpActivity.string2MD5(mpasswd);
				new LoginTask().execute();
				mDialog=new ProgressDialog(LoginActivity.this);
				mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mDialog.setMessage("登录中...");
				mDialog.setIndeterminate(false);
				mDialog.setCancelable(false);
				Window window=mDialog.getWindow();
				WindowManager.LayoutParams lp = window.getAttributes();
				lp.alpha=0.8f;
				window.setAttributes(lp);
				mDialog.show();
			}
		});
	}

	private class LoginTask extends AsyncTask<Void, Void, JSONObject>
	{

		@Override
		protected JSONObject doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			JSONObject json;
			String path=PositiveEnergyActivity.UrlHead
					+"search.action?claseName=UserSrvImpl&invokeMethod=login"
					+"&param.phone="+mphonenum
					+"&param.password="+mpasswd;
			try{
				byte[] data = ReadParseClass.readParse(path);
				String s=new String(data);
				json = new JSONObject(s);

				return json;
			}catch(Exception e){}
			return null;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			mDialog.cancel();
			// TODO Auto-generated method stub
			if((Object)result==null)
			{
				Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_LONG).show();
				return;
			}
			try {
				if( result.getInt("statusCode") == 200 ) {
					Log.d(TAG, result.toString());
					Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_LONG).show();
					SharedPreferences.Editor editor = pre.edit(); 
					editor.putString(Const.USER_PHONE_NUMBER, mphonenum);
					editor.putString(Const.USER_PASSWD, mpasswdorigen);
					
					JSONObject json=result.getJSONObject("user");
					editor.putString(Const.USER_SCHOOL, json.getString("school"));
					editor.putString(Const.USER_COLLEGE, json.getString("college"));
					editor.putString(Const.USER_ID, json.getString("id"));
					editor.putString(Const.USER_NAME, json.getString("nickname"));
					editor.putString(Const.USER_AVATAR_URL, json.optString("pic_url"));
					// 将用户是否登陆的判断变量置为 true
					editor.putBoolean(Const.USER_LOG_IN_OR_NOT, true);
					editor.commit();
					
					// 跳转到 主界面 
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.fadein, R.anim.fadeout);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			super.onPostExecute(result);
		}
	}

}
