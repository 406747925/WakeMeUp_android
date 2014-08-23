package cn.jlu.ge.getup.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.net.sip.SipSession.Listener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import cn.jlu.ge.getup.tools.Const;

@SuppressLint("HandlerLeak") public class NetworkTask <Token> extends HandlerThread {

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
	
	public NetworkTask(Handler responseHandler) {
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
					Log.i(TAG, "Got a request for url: " + requestMap.get(token));
					handleRequest(token);
				}
			}
			
		};
	}
	
	public void queueTask(Token token, String url) {
		Log.i(TAG, "Got an task URL: " + url);
		requestMap.put(token, url);
		
		networkHandler.obtainMessage(Const.MESSAGE_NETWORK_GET_FIVEDAYS_WEATHER, token).sendToTarget();
	}
	
	private void handleRequest(final Token token) {
		// TODO Auto-generated method stub
		final String url = requestMap.get(token);
		if ( url == null )
			return ;
		
		final String response = getFiveDaysWeatherFromNet(url);
		Log.i(TAG, ">>>>" + response);
		
		mainThreadResponseHandler.post(new Runnable () {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if ( requestMap.get(token) != url )
					return ;
				
				requestMap.remove(token);
				fiveDaysWeatherlistener.onFiveDaysWeatherGot(token, response);
				
			}
			
		});
		
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
