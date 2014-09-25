package cn.jlu.ge.dreamclock.tools;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class ReadParseClass {
	private final static String TAG = "ReadParseClass";
	public static byte[] readParse(String urlPath) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "text/html");
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
        conn.setRequestProperty("Accept-Charset", "UTF-8");
   //     conn.setRequestProperty("contentType", "application/x-www-form-urlencoded; charset=utf-8");
        try{
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
                outStream.write(data, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
        }catch(Exception e) {
        	Log.e( TAG, e.toString() );
        }
        finally{
        	conn.disconnect();
        	
        }

        return null;
        
}
	
	public static byte[] readParse(String urlPath,String extra) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setDoInput(true);
        
        StringBuffer params = new StringBuffer();
        // 表单参数与get形式一样
        params.append(extra);
        byte[] bypes = params.toString().getBytes();
        
   //     conn.setRequestProperty("contentType", "application/x-www-form-urlencoded; charset=utf-8");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// 请求头, 必须设置
    //    conn.setRequestProperty("Content-Length", data.length + "");// 注意是字节长度, 不是字符长度
        
        try{
        conn.getOutputStream().write(bypes);// 输入参数
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
        try{
        	InputStream inStream = null;
        	try {
        		inStream = conn.getInputStream();
        	} catch ( Exception e ) {
        		
        		Log.d(TAG, ">>>> get it : " + e.toString());
        		
        	}
        	
	        while ((len = inStream.read(data)) != -1) {
	                outStream.write(data, 0, len);
	        }
	        Log.e(TAG, ">>>> before close.");
	        inStream.close();
	        return outStream.toByteArray();
        
        }catch(Exception e) {
        	Log.e( TAG, ">>>> outStream: " + e.toString() );
        }
        finally{
        	conn.disconnect();
        }


        return null;
        
}
}
