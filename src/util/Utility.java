package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import model.City;
import model.CoolWeatherDB;
import model.County;
import model.Province;

public class Utility {
	/**
	* 解析和处理服务器返回的省级数据
	*/ 
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) { 
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(","); 
			if (allProvinces != null && allProvinces.length > 0) { 
				for (String p : allProvinces) {
					String[] array = p.split("\\|"); 
					Province province = new Province(); 
					province.setProvinceCode(array[0]); 
					province.setProvinceName(array[1]);
					// 将解析出来的数据存储到Province表
					coolWeatherDB.saveProvince(province);
				} return true;
			} 
		} return false;
	}
		/**
		* 解析和处理服务器返回的市级数据
		*/ 
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response, int provinceId) { 
		if (!TextUtils.isEmpty(response)) { 
			String[] allCities = response.split(","); 
			if (allCities != null && allCities.length > 0) { 
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]); 
					city.setCityName(array[1]); 
					city.setProvinceId(provinceId);
					// 将解析出来的数据存储到City表
					coolWeatherDB.saveCity(city);
				} return true;
			} 
		} 
		return false;
	}
		/**
		* 解析和处理服务器返回的县级数据
		*/ 
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response, int cityId) { 
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(","); 
			if (allCounties != null && allCounties.length > 0) { 
				for (String c : allCounties) {
					String[] array = c.split("\\|"); 
					County county = new County(); 
					county.setCountyCode(array[0]); 
					county.setCountyName(array[1]); 
					county.setCityId(cityId);
					// 将解析出来的数据存储到County表
					coolWeatherDB.saveCounty(county);
				} 
				return true;
			} 
		} 
		return false;
	}
	
	/**
	* 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
	*/ 
	public static void handleWeatherResponse(Context context,String response){ 
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject data = jsonObject.getJSONObject("data");
			String cityName = data.getString("city");
			JSONArray forecast=data.getJSONArray("forecast");
			JSONObject array1=forecast.getJSONObject(0);
			String temp1=array1.getString("high");
			String t[]=temp1.split(" ");
			temp1=t[1];
            String temp2=array1.getString("low");
            t=temp2.split(" ");
            temp2=t[1];
            String weatherDesp=array1.getString("type");
            String publishTime=array1.getString("date");
			saveWeatherInfo(context, cityName,temp1, temp2,weatherDesp, publishTime);
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	* 将服务器返回的所有天气信息存储到SharedPreferences文件中。
	*/ 
	public static void saveWeatherInfo(Context context, String cityName,String temp1, String temp2, String weatherDesp, String publishTime) {
		//获取当前时间
		long time=System.currentTimeMillis();
		final Calendar mCalendar=Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		String mHour,mMinuts;
		mHour=String.valueOf(mCalendar.get(Calendar.HOUR));
		mMinuts=String.valueOf(mCalendar.get(Calendar.MINUTE));
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		editor.putBoolean("city_selected", true); 
		editor.putString("city_name", cityName); 
		editor.putString("temp1", temp1); 
		editor.putString("temp2", temp2); 
		editor.putString("weather_desp", weatherDesp); 
		editor.putString("publish_time", mHour+":"+mMinuts); 
		editor.putString("current_date", sdf.format(new Date())); 
		editor.commit();
		
	}

}
