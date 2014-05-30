package cn.jlu.ge.getup;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import cn.jlu.ge.getup.tools.GetUpVibrator;
import cn.jlu.ge.getup.tools.ShakeDetector;
import cn.jlu.ge.getup.tools.ShakeDetector.OnShakeListener;


public class WakeUpActivity extends Activity {

	GetUpVibrator vibrator;
	ShakeDetector shakeDetector;
	
	TextView tv;
	TextView welcome;
	int shakeTimes;
	
	// �������̹�����
	KeyguardManager mKeyguardManager = null;
	// ������Դ������
	private PowerManager pm;
	private PowerManager.WakeLock wakeLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wakeup);
		
		// ������Ļ�������ֻ�

		setScreenBrightAndShowWindow();
		
        // ��ʼ��ҡ�δ���
		shakeTimes = 3;
        long[] pattern = {1000, 500, 1000, 500};
        int type = 0;
        
        tv = (TextView)findViewById(R.id.tv);
        tv.setText("���ӽ���ʣ��ҡ�δ�����" + shakeTimes);
        
//        welcome = (TextView)findViewById(R.id.welcome);
//        welcome.setText("���ѵ������룬ҡ�ѵ��Ǿ���");
        
        vibrator = new GetUpVibrator(getApplicationContext());
		vibrator.playVibrate(pattern, type);
		shakeDetector = new ShakeDetector(getApplicationContext());
		shakeDetector.start();
        shakeDetector.registerOnShakeListener(new OnShakeListener(){
			@Override
			public void onShake() {
				// TODO Auto-generated method stub
				shakeTimes--;
				if (shakeTimes == 0) {
					shakeDetector.stop();
					vibrator.cancelVibrate();
				}
				tv.setText("���ӽ���ʣ��ҡ�δ�����" + shakeTimes);
			}
        });
	}
	
	public void setScreenBrightAndShowWindow() {
		final Window win = (Window) this.getWindow();
		final WindowManager.LayoutParams params = win.getAttributes();
		params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

		//��ȡ��Դ�ķ���
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//��ȡϵͳ����
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		//��������
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		wakeLock.acquire();// ����
		wakeLock.release();// �ͷŵ���

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
