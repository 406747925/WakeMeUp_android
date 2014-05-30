package cn.jlu.ge.getup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.getup.tools.AlarmReceiver;
import cn.jlu.ge.getup.tools.DBAdapter;
import cn.jlu.ge.getup.tools.FileIOTools;
import cn.jlu.ge.getup.tools.ForegroundService;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MainActivity extends SherlockActivity {
	
//	GetUpVibrator vibrator;
//	ShakeDetector shakeDetector;
	static FileIOTools fileIO;
	private DBAdapter db;
	private AsyncHttpClient client;
	
	private Calendar calendar;
	private RelativeLayout alarmLayout;
	private RelativeLayout weatherLayout;
	private RelativeLayout signInLayout;
	private RelativeLayout positiveLayout;
	private AlarmManager alarms;
	int shakeTimes;
	
	private TextView rowOne;
	private TextView rowTwo;
	private TextView rowThree;
	private ImageView weatherIcon;
	
	private ImageButton locationBtn;
	private ImageButton tempBtn;
	private ImageButton windBtn;
	private ImageButton uvValueBtn;
	private ImageButton weatherLikeBtn;
	private TextView locationText;
	private TextView tempText;
	private TextView windText;
	private TextView uvValueText;
	private TextView weatherLikeText;
	
	private TextView signInText;
	
	private TextView positiveText;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("hh mm");;
	String ALARM_FILE_NAME = "Alarm_File";
	final String SNOW_AND_RAIN = "���ѩ";
	final String SUNNY = "��";
	final String CLOUD_LITTLE_RAIN = "����תС��";
	final String SUNNY_CLOUD = "��ת����";
	final String CLOUD = "����";

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        startService(foregroundServiceIntent);
        
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        
        alarmLayout = (RelativeLayout) findViewById(R.id.alarm);
        weatherLayout = (RelativeLayout) findViewById(R.id.weather);
        signInLayout = (RelativeLayout) findViewById(R.id.signInLayout);
        positiveLayout = (RelativeLayout) findViewById(R.id.positiveLayout);
        
        // ����
        calendar = Calendar.getInstance();

        // �������һ����ʱ�䡢��������һ������
        // ʱ����ʾ�͸���
        rowOne = (TextView) findViewById(R.id.rowOne);
        
        handler.post(updateThread);
        
        rowTwo = (TextView) findViewById(R.id.rowTwo);
        rowThree = (TextView) findViewById(R.id.rowThree);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);

        // ������ڶ������ص㡢���������١��¶ȡ�UV��ͼ����ֵ
        locationBtn = (ImageButton) findViewById(R.id.location);
        weatherLikeBtn = (ImageButton) findViewById(R.id.weatherLike);
        windBtn = (ImageButton) findViewById(R.id.wind);
        tempBtn = (ImageButton) findViewById(R.id.temp);
        uvValueBtn = (ImageButton) findViewById(R.id.uvValue);
        
        locationText = (TextView) findViewById(R.id.locationText);
        weatherLikeText = (TextView) findViewById(R.id.weatherLikeText);
        windText = (TextView) findViewById(R.id.windText);
        tempText = (TextView) findViewById(R.id.tempText);
        uvValueText = (TextView) findViewById(R.id.uvValueText);
        
        
        // �����������������ǩ�������
        signInText = (TextView) findViewById(R.id.signInText);
        signInText.setText("����ǩ��");
        
        // �������������������վ
        positiveText = (TextView) findViewById(R.id.positiveText);

        alarmLayout.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				
//				Intent intent = new Intent(MainActivity.this, SignInActivity.class);
//				startActivity(inten	t);
				
			}
        });
        
        client = new AsyncHttpClient();
        getWeatherFromNet();
        
        // ������ʱ����ʹ��
		positiveText.setText("MATLAB��ʼ����������");
		
        client.get("", new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable throwable, String response) {
				// TODO Auto-generated method stub
				super.onFailure(throwable, response);
			}

			@Override
			public void onSuccess(int throwable, String response) {
				// TODO Auto-generated method stub
				super.onSuccess(throwable, response);
			}
        	
        });
    }
    
    void internetConnect() {
    	try {

			String data = "";//����Ҫ������ַ���
			//baseUrl
			String baseUrl = "http://www.weather.com.cn/data/sk/101010100.html";

			//��URL�����ƴ��
			HttpGet getMethod = new HttpGet(baseUrl);

			HttpClient httpClient = new DefaultHttpClient();

			try {
			    HttpResponse response = httpClient.execute(getMethod); //����GET����  
			    data = EntityUtils.toString(response.getEntity(), "utf-8");
			    Log.i("resCode", "resCode = " + response.getStatusLine().getStatusCode()); //��ȡ��Ӧ��  
			    Log.i("result", "result = " + data);//��ȡ��������Ӧ���� 
			} catch (ClientProtocolException e) {
			    // TODO Auto-generated catch block
				Toast.makeText( MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
			    e.printStackTrace();
			} catch (IOException e) {
			    // TODO Auto-generated catch block
				Toast.makeText( MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
			    e.printStackTrace();
			}

			Toast.makeText( MainActivity.this, data, Toast.LENGTH_SHORT).show();

    	} catch (Exception e) {
    		Log.v("Log e", e.toString());
            e.printStackTrace();
    	}
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        switch(item.getItemId()){ 
        case R.id.alarm_setting:
        	Intent intent = new Intent(MainActivity.this, SetAlarmActivity.class);
        	startActivity(intent);
            break;
            
        }
		return super.onOptionsItemSelected(item);
	}

	// ˢ��ʱ��
	Handler handler = new Handler();
	Runnable updateThread = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(updateThread, 10000);
			String time = sdf.format(Calendar.getInstance().getTime());
			rowOne.setText(time);
		}
		
	};
	
	
	public void writeFileData(String fileName,String message){ 
		try{ 
			// MODE_PRIVATE
				FileOutputStream fout = openFileOutput(fileName, MODE_APPEND);
		        byte [] bytes = message.getBytes(); 
		        fout.write(bytes); 
		        fout.close();
	
			} catch(Exception e){ 
	        	e.printStackTrace(); 
	        }
	}
	
	void getWeatherFromNet() {
        client.get("http://www.weather.com.cn/data/cityinfo/101060101.html", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	try {
            		
					JSONObject weatherObject = new JSONObject(response).getJSONObject("weatherinfo");
					String cityStr = weatherObject.getString("city");
					String temp1Str = weatherObject.getString("temp1");
					String temp2Str = weatherObject.getString("temp2");
					String weatherStr = weatherObject.getString("weather");
					String ptimeStr = weatherObject.getString("ptime");
					
					rowTwo.setText(cityStr);
					rowThree.setText(temp2Str + "~" + temp1Str + "\n" + weatherStr);
					
					if (weatherStr.equals(SNOW_AND_RAIN)) {
						weatherIcon.setBackgroundResource(R.drawable.snow_with_rain);
					}
					else if (weatherStr.equals(SUNNY)) {
						weatherIcon.setBackgroundResource(R.drawable.sunny);
					}
					else if (weatherStr.equals(CLOUD_LITTLE_RAIN)) {
						weatherIcon.setBackgroundResource(R.drawable.cloud_little_rain);
					}
					else if (weatherStr.equals(SUNNY_CLOUD)) {
						weatherIcon.setBackgroundResource(R.drawable.sunny_cloud);
					}
					else if (weatherStr.equals(CLOUD)) {
						weatherIcon.setBackgroundResource(R.drawable.cloudy);
					}
					else {
						weatherIcon.setBackgroundResource(R.drawable.error_weather);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

			@Override
			public void onFailure(Throwable throwable, String failureStr) {
				// TODO Auto-generated method stub
				sdf = new SimpleDateFormat("hh:mm");    
				String time = sdf.format(new java.util.Date());
				rowOne.setText(time);
				super.onFailure(throwable, failureStr);
			}
        });
        
        client.get("http://www.weather.com.cn/data/sk/101060101.html", new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
			}

			@Override
			public void onSuccess(int code, String response) {
				// TODO Auto-generated method stub
				try {
					
					JSONObject weatherObject = new JSONObject(response).getJSONObject("weatherinfo");
					String cityStr = weatherObject.getString("city");
					String tempStr = weatherObject.getString("temp");
					String windStr = weatherObject.getString("WS");
					String wetStr = weatherObject.getString("SD");
					String wdStr = weatherObject.getString("WD");
					
			        locationText.setText(cityStr);
			        weatherLikeText.setText(windStr);
			        windText.setText(wetStr);
			        tempText.setText(tempStr + "C");
			        uvValueText.setText(wdStr);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				super.onSuccess(code, response);
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.sign_in, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
}
