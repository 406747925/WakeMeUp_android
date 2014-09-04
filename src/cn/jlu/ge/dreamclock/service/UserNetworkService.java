package cn.jlu.ge.dreamclock.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.Xml;
import cn.jlu.ge.dreamclock.network.NetworkTaskThread;
import cn.jlu.ge.dreamclock.tools.Const;

import com.loopj.android.http.AsyncHttpClient;

public class UserNetworkService extends Service {
	final String TAG = "UserNetworkService";
	
	private NetworkTaskBinder networkTaskBinder;
	private AsyncHttpClient client;
	String[] weatherList;
	String returnResponse;
	
	NetworkTaskThread<String[]> networkTask;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		networkTaskBinder = new NetworkTaskBinder();
		networkTask = new NetworkTaskThread<String[]>( new Handler() );
		networkTask.setListener(new NetworkTaskThread.Listener<String[]>() {

			@Override
			public void onFiveDaysWeatherGot(String[] token, String response) {
				
				// TODO Auto-generated method stub
				Log.v(TAG, ">>>>got it: " + response);
			}
			
		});
		
		networkTask.start();
		networkTask.getLooper();
		
		Log.v(TAG, "networkTaskBinder has been created.");
		
	}

	@Override
	public void onDestroy() {
		
		// TODO Auto-generated method stub
		networkTaskBinder = null;
		
		networkTask.quit();
		
		super.onDestroy();
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		// TODO Auto-generated method stub
		
		if ( networkTaskBinder == null ) {
			Log.v(TAG, "networkTaskBinder: " + networkTaskBinder.toString());
		} else {
			Log.v(TAG, "networkTaskBinder not null.");
			if ( intent.getAction().equals(Const.GET_FIVE_DAYS_WEATHER) ) {
				
			}
		}
		
		
		return networkTaskBinder;
	}
	
	
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}



	public class NetworkTaskBinder extends INetworkTask.Stub {

		@Override
		public 	String getFiveDaysWeather (final String weatherCity) {
			
			
			return returnResponse;

		}

		@Override
		public void getFiveDaysWeatherFromNet(final String weatherCity)
				throws RemoteException {
			// TODO Auto-generated method stub
			
			Log.v( TAG, "Network start." );
			networkTask.queueTask( weatherList, weatherCity );
			Log.v( TAG, ">>> 2 : " + System.currentTimeMillis() );

			
		}
		
	}
	
	
	
	void setFiveDaysWeatherDataInDb ( String[] weatherList ) {
		
	}

	
	String[] getFiveDaysWeatherAndSuggestion (String response) {
		
		ArrayList<String> weatherList = new ArrayList<String>();
		
		try {
			XmlPullParser parser = Xml.newPullParser();
			InputStream inputStreamWithResponse = new ByteArrayInputStream(response.getBytes("UTF-8"));
			parser.setInput(inputStreamWithResponse, "utf-8");
			int event = parser.getEventType();

			while ( event != XmlPullParser.END_DOCUMENT ) {
				switch ( event ) {
				case XmlPullParser.START_TAG:
					if ( "string".equals( parser.getName() ) ) {
						String item = parser.nextText();
						weatherList.add(item);
					} else if ( "ArrayOfString".equals( parser.getName() ) ) {
					}
				break;
				
				default: break;
				}
				
				event = parser.next();
			}
			
			if ( weatherList.size() > 20 ) {
				
//				setFiveDaysWeatherView(weatherList);
//				setFiveDaysWeatherDataInDb(weatherList);
//				setTodayWeatherSuggestionView( weatherList.get(6) );
//				setSecAndThirdWeatherCitiesView();
				
				return (String[]) weatherList.toArray();
			}
			
			return null;
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			Log.v(TAG, "XmlPullParserException: " + e.toString());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
