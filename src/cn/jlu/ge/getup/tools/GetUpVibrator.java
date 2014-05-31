package cn.jlu.ge.getup.tools;

import android.content.Context;
import android.os.Vibrator;

public class GetUpVibrator{
	Vibrator vibrator;
    //long[] pattern 数组中数字的含义依次是静止的时长，震动时长，静止时长，震动时长。时长的单位是毫秒
	long[] pattern = { 1000, 500, 1000, 500};
    int type = -1;

	public GetUpVibrator(Context context) {
    	this.vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    }
    
	public int playVibrate(long[] pattern, int type) {
    	this.vibrator.vibrate(pattern, type);
    	return 0;
    }
    
    public int playDefaultVibrate() {
    	this.vibrator.vibrate(pattern, type);
		return 0;
    }
    
    public int cancelVibrate() {
        this.vibrator.cancel();        	
    	return 0;
    }
}
