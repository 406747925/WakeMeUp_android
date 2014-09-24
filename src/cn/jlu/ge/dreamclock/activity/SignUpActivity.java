package cn.jlu.ge.dreamclock.activity;

import java.net.URLEncoder;
import java.security.MessageDigest;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.ReadParseClass;
import cn.jlu.ge.dreamclock.tools.UploadPhoneNumberTask;

public class SignUpActivity extends Activity {
	private String musername;
	private String mpasswd;
	private String mconfirmpasswd;
	private String msex;
	private String mschool;
	private String mphonenum;
	private String mcollage;
	private ProgressDialog mDialog;
	final String TAG = "SignUpActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 注册登陆
		checkSignUpOrNot();
		
		setContentView(R.layout.activity_sign_up);
		getActionBar().hide();
		msex="m";
		View view=findViewById(R.id.textViewsignin);
		view.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				finish();

			}
		});
		RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup);
		//绑定一个匿名监听器
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				//获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton)findViewById(radioButtonId);
				//更新文本内容，以符合选中项
				msex=rb.getText().toString().equals("男")?"m":"f";
			}
		});

		Button button=(Button)findViewById(R.id.buttonsignup);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try{
					EditText username=(EditText)findViewById(R.id.editText1);
					musername=username.getText().toString();
					musername=URLEncoder.encode(musername, "UTF-8");
					EditText passwd=(EditText)findViewById(R.id.editText4);
					mpasswd=passwd.getText().toString();
					//	mpasswd=URLEncoder.encode(mpasswd, "UTF-8");
					EditText confirmpasswd=(EditText)findViewById(R.id.editText5);
					mconfirmpasswd=confirmpasswd.getText().toString();
					//					mconfirmpasswd=URLEncoder.encode(mconfirmpasswd, "UTF-8");
					EditText school=(EditText)findViewById(R.id.editText2);
					mschool=school.getText().toString();
					mschool=URLEncoder.encode(mschool, "UTF-8");
					EditText phone=(EditText)findViewById(R.id.editText3);
					mphonenum=phone.getText().toString();
					mphonenum=URLEncoder.encode(mphonenum, "UTF-8");
					EditText collage=(EditText)findViewById(R.id.editText6);
					mcollage=collage.getText().toString();
					mcollage=URLEncoder.encode(mcollage, "UTF-8");
				}catch(Exception e){}
				// TODO Auto-generated method stub
				if(mpasswd.equals(mconfirmpasswd))
				{
					mpasswd=string2MD5(mpasswd);
					new RegisterTask().execute();
					mDialog=new ProgressDialog(SignUpActivity.this);
					mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					mDialog.setMessage("请稍后...");
					mDialog.setIndeterminate(false);
					mDialog.setCancelable(false);
					Window window=mDialog.getWindow();
					WindowManager.LayoutParams lp = window.getAttributes();
					lp.alpha=0.8f;
					window.setAttributes(lp);
					mDialog.show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "密码输入不一致", Toast.LENGTH_SHORT).show();;
				}

			}
		});
	}
	
	public void checkSignUpOrNot () {
		SharedPreferences appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
		boolean signUpOrNot = appInfo.getBoolean(Const.USER_LOG_IN_OR_NOT, false);
		
		appInfo = null;
		
		if ( signUpOrNot == true ) {
			Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
			startActivity(intent);
			this.finish();
		}
	}

	private class RegisterTask extends AsyncTask<Void, Void, JSONObject> {
		
		@Override
		protected JSONObject doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			JSONObject json;
			getApplicationContext();
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String path=PositiveEnergyActivity.UrlHead
					+"search.action?claseName=UserSrvImpl&invokeMethod=insert"
					+"&param.phone="+mphonenum
					+"&param.password="+mpasswd
					+"&param.nickname="+musername
					+"&param.school="+mschool
					+"&param.college="+mcollage
					+"&param.device_id="+tm.getDeviceId()
					+"&param.gender="+msex;
			
			Log.v(TAG, path);
			
			try{
				byte[] data =  ReadParseClass.readParse(path);
				String s=new String(data);
				Log.v(TAG, s);
				json = new JSONObject(s);

				return json;
			}catch(Exception e){}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			mDialog.cancel();
			if((Object)result==null)
			{
				Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_LONG).show();
				return;
			}
			// TODO Auto-generated method stub
			try {
				if(result.getInt("statusCode")==200)
	//			if(true)
				{
					Log.v(TAG, result.toString());
					Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
					SharedPreferences pre = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
					Editor editor=pre.edit();
					editor.putString( Const.USER_ID, result.getString("id") );
					editor.putString(Const.USER_NAME, musername);
					editor.putBoolean( Const.USER_LOG_IN_OR_NOT, true );
					editor.commit();
					////////////////////上传手机列表
	                new UploadPhoneNumberTask(getApplicationContext()).execute();
					////////////////////
	                
	                // 跳转到 主界面
	                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
	                startActivity(intent);
	                finish();
                
				}else
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

    public static String string2MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();
  
    }
    

    
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Log.v(TAG, "finish()");
		
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		am.killBackgroundProcesses(getPackageName());
		
		super.finish();
		
	}
}
