package service;

import receiver.AutoUpdateReceiver;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import activity.WeatherActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) { 
		Log.d("aaa", "��������");
		new Thread(new Runnable() {
			@Override public void run() { 
				updateWeather();
			}
		}).start();
		//********************************************************************************
		intent = new Intent();
		intent.setAction("mybroadcast");
		Log.d("aaa","���͹㲥" );
		this.sendBroadcast(intent);
		
		AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
		int anHour = 6 * 60 * 60 * 1000; // ����6Сʱ�ĺ�����
		//int anHour = 20 * 1000; // ����20S�ĺ�����
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour; 
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);  
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi); 
		return super.onStartCommand(intent, flags, startId);
	}
	/**
	* ����������Ϣ��
	*/ 
	private void updateWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String countyCode = prefs.getString("county_code", "");
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override 
			public void onFinish(final String response) { 
				 if (!TextUtils.isEmpty(response)) { 
					 // �ӷ��������ص������н�������������
					 String[] array = response.split("\\|"); 
					 if (array != null && array.length == 2) { 
							String weatherCode = array[1]; 
							String address = "http://wthrcdn.etouch.cn/weather_mini?citykey="+weatherCode;
							HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
								@Override 
								public void onFinish(final String response) { 
									// ������������ص�������Ϣ
									Utility.handleWeatherResponse(AutoUpdateService.this,response); 
								}
								@Override
								public void onError(Exception e) {
									// TODO Auto-generated method stub
									
								}
							});
					 }
				 }
			}
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub			
			} 
		});	
			
	}
}
