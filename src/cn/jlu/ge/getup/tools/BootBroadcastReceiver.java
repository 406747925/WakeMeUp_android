package cn.jlu.ge.getup.tools;

import cn.jlu.ge.getup.service.ForegroundService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
	//��дonReceive����
	@Override
	public void onReceive(Context context, Intent intent) {
	//��ߵ�XXX.class����Ҫ�����ķ���
	Intent service = new Intent(context, ForegroundService.class);
	context.startService(service);
	Log.v("TAG", "�����Զ������Զ�����.....");
	//����Ӧ�ã�����Ϊ��Ҫ�Զ�������Ӧ�õİ���
//	Intent newIntent = getPackageManager().getLaunchIntentForPackage("cn.jlu.ge.getup");
//	context.startActivity(newIntent );
	}
}