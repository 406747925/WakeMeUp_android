package cn.jlu.ge.dreamclock.tools;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;

public class CavansImageTask extends AsyncTask<View, Void, Drawable>{
    private View mView;
    static HashMap<String, SoftReference<Drawable>> imageCache= new HashMap<String, SoftReference<Drawable>>();
 
    public CavansImageTask() {
       // imageCache = new HashMap<String, SoftReference<Drawable>>();
    }
 
    protected Drawable doInBackground(View... views) {
        Drawable drawable = null;
        View view = views[0];
        this.mView = view;
        if (view.getTag() != null) {
            if (imageCache.containsKey(view.getTag())) {
                SoftReference<Drawable> cache = imageCache.get(view.getTag().toString());
                drawable = cache.get();
                if (drawable != null) {
                    return drawable;
                }
            }
            try {
                if (URLUtil.isHttpUrl(view.getTag().toString())) {// 如果为网络地址。则连接url下载图片
                    URL url = new URL(view.getTag().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    drawable = Drawable.createFromStream(stream, "src");
                    stream.close();   
                } else {// 如果为本地数据，直接解析
                    drawable = Drawable.createFromPath(view.getTag().toString());
                }
            } catch (Exception e) {
                Log.v("img", e.getMessage());
                return null;
            }
            finally
            {
            	SoftReference<Drawable> d=new SoftReference<Drawable>(drawable);
            	imageCache.put(view.getTag().toString(), d);
            }
        }
        return drawable;
    }
	protected void onPostExecute(Drawable drawable) {
        if (drawable != null) {
        	ImageView imageView=(ImageView)mView;
        	imageView.setImageDrawable(drawable);
            this.mView = null;
        }
    }
 

}
