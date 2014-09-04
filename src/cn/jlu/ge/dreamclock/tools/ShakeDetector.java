package cn.jlu.ge.dreamclock.tools;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class ShakeDetector implements SensorEventListener {

	// ʱ����, 100ms
	static final int UPDATE_INTERVAL = 100;
	// ��һ�μ���ʱ��
	long lastUpdateTime;
 
	// ��һ�μ��ʱ�����ٶ���x��y��z�����ϵķ��������ں͵�ǰ���ٶȱȽ��� 
    float lastX, lastY, lastZ;
    Context context;
    SensorManager sensorManager;
    ArrayList<OnShakeListener> listeners;
    public int shakeThreshold = 2000;
    
    public ShakeDetector(Context c) {
        context = c;
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        listeners = new ArrayList<OnShakeListener>();
    }
    
    public interface OnShakeListener {
        /**
         * ���ֻ�ҡ��ʱ������
         */
        void onShake();
    }
    /**
     * ע��OnShakeListener����ҡ��ʱ����֪ͨ
     *
     * @param listener
     */
    public void registerOnShakeListener(OnShakeListener listener) {
        if (listeners.contains(listener))  
            return;  
        listeners.add(listener);  
    }  
    /** 
     * �Ƴ��Ѿ�ע���OnShakeListener 
     *  
     * @param listener 
     */
    public void unregisterOnShakeListener(OnShakeListener listener) {  
        listeners.remove(listener);  
    }
    /** 
     * ����ҡ�μ��
     */
    public void start() {
        if (sensorManager == null) {  
            throw new UnsupportedOperationException();
        }
        Sensor sensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            throw new UnsupportedOperationException();  
        }
        boolean success = sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_GAME);  
        if (!success) {
            throw new UnsupportedOperationException();  
        }
    }
    /** 
     * ֹͣҡ�μ�� 
     */  
    public void stop() {  
        if (sensorManager != null)  
            sensorManager.unregisterListener(this);  
    }  
    @Override  
    public void onAccuracyChanged(Sensor sensor, int accuracy) {  
        // TODO Auto-generated method stub  
    }  
    @Override
    public void onSensorChanged(SensorEvent event) {  
        long currentTime = System.currentTimeMillis();  
        long diffTime = currentTime - lastUpdateTime;  
        if (diffTime < UPDATE_INTERVAL) return ;
        lastUpdateTime = currentTime;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float deltaX = x - lastX;
        float deltaY = y - lastY;  
        float deltaZ = z - lastZ;  
        lastX = x;
        lastY = y;
        lastZ = z;
        float delta = FloatMath.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)  
                / diffTime * 10000;
        if (delta > shakeThreshold) { // �����ٶȵĲ�ֵ����ָ������ֵ����Ϊ����һ��ҡ��  
            this.notifyListeners();
        }
    }
    /** 
     * ��ҡ���¼�����ʱ��֪ͨ���е�listener
     */
    private void notifyListeners() {
        for (OnShakeListener listener : listeners) {
            listener.onShake();
        }
    }
}
