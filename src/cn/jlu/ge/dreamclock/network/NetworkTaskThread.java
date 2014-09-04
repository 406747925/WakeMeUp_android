package cn.jlu.ge.dreamclock.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import cn.jlu.ge.dreamclock.tools.Const;

@SuppressLint("HandlerLeak") public class NetworkTaskThread <Token> extends HandlerThread {

	private static final String TAG = "NetworkTask";
	Handler networkHandler;
	Map <Token, String> requestMap = 
			Collections.synchronizedMap( new HashMap<Token, String>() );
	Handler mainThreadResponseHandler;
	Listener<Token> fiveDaysWeatherlistener;
	
	public interface Listener<Token> {
		void onFiveDaysWeatherGot(Token token, String response);
	}
	
	public void setListener (Listener<Token> listener) {
		fiveDaysWeatherlistener = listener;
	}
	
	public NetworkTaskThread(Handler responseHandler) {
		// TODO Auto-generated constructor stub
		super(TAG);
		mainThreadResponseHandler = responseHandler;
		
	}
	
	@Override
	protected void onLooperPrepared() {
		// TODO Auto-generated method stub
//		super.onLooperPrepared();
		
		networkHandler = new Handler () {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
//				super.handleMessage(msg);
				
				if ( msg.what == Const.MESSAGE_NETWORK_GET_FIVEDAYS_WEATHER ) {
					
					@SuppressWarnings("unchecked")
					Token token = (Token) msg.obj;
					String url = "http://www.webxml.com.cn/WebServices/getWeather?theCityCode=" + requestMap.get(token) + "&theUserId=";
					Log.i( TAG, "Got a request for url: " + url);
					handleRequest(token);
				}
			}
			
		};
	}
	
	public void queueTask(Token token, String url) {
		Log.i(TAG, "Got an task URL: " + url);
		requestMap.put( token, url);
		networkHandler.obtainMessage( Const.MESSAGE_NETWORK_GET_FIVEDAYS_WEATHER, token).sendToTarget();
	}
	
	void getFiveDaysWeatherFromNet () {
		
	}
	
	private void handleRequest(final Token token) {
		// TODO Auto-generated method stub
		final String url = requestMap.get(token);
		if ( url == null )
			return ;
		
		final String response = getFiveDaysWeatherFromNet(url);
		Log.i(TAG, ">>>>" + response);
		
		mainThreadResponseHandler.postDelayed(new Runnable () {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if ( requestMap.get(token) != url )
					return ;
				
				Log.v( TAG, ">>> 1 : " + System.currentTimeMillis() );
				requestMap.remove(token);
				fiveDaysWeatherlistener.onFiveDaysWeatherGot(token, response);
				
			}
			
		}, 500);
		
	}
	
	public String getFiveDaysWeatherFromNet (String url) {
		// TODO 从网络获取响应字符窜
		
		return "what's happened.";
	}
	
	public void clearQueue () {
		networkHandler.removeMessages(Const.MESSAGE_NETWORK_GET_FIVEDAYS_WEATHER);
		requestMap.clear();
	}
	
	
}
