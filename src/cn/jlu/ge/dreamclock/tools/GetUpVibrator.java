package cn.jlu.ge.dreamclock.tools;

import android.content.Context;
import android.os.Vibrator;

public class GetUpVibrator{
	Vibrator vibrator;
    //long[] pattern ���������ֵĺ��������Ǿ�ֹ��ʱ������ʱ������ֹʱ������ʱ����ʱ���ĵ�λ�Ǻ���
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
