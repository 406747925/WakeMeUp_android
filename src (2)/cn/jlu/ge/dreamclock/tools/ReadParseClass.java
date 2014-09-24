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
        conn.setRequestMethod("POST");
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
}
