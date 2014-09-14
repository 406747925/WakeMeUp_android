package cn.jlu.ge.dreamclock.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jlu.ge.dreamclock.R;

public class BindDataAndResource {
	
	static public int getWeatherIconImageResourceId (String weatherStr) {
		if (weatherStr == null) {
			return R.drawable.error_weather;
		}
		else if (weatherStr.equals( Const.SNOW_AND_RAIN ) ) {
			return R.drawable.little_snow;
		}
		else if (weatherStr.equals( Const.SUNNY ) ) {
			return R.drawable.sunny;
		}
		else if (weatherStr.equals( Const.CLOUD_LITTLE_RAIN ) ) {
			return R.drawable.little_rain;
		}
		else if (weatherStr.equals( Const.SUNNY_CLOUD ) ) {
			return R.drawable.cloudy;
		}
		else if (weatherStr.equals( Const.CLOUD ) ) {
			return R.drawable.cloudy;
		}
		else if (weatherStr.equals( Const.RAIN_WITH_THUNDER ) ) {
			return R.drawable.tundering_and_raining;
		}
		else if (weatherStr.equals( Const.SOMETIME_RAIN ) ) {
			return R.drawable.little_rain;
		}
		else if (weatherStr.equals( Const.CLOUD_TO_SOMETIME_RAIN ) ) {
			return R.drawable.little_rain;
		}
		else if (weatherStr.equals( Const.THOUNDER_AND_RAIN ) ) {
			return R.drawable.tundering_and_raining;
		}
		else if (weatherStr.equals( Const.RAIN_AND_THOUNDER ) ) {
			return R.drawable.tundering_and_raining;
		}
		else if (weatherStr.equals( Const.RAIN_TO_CLOUDY) ) {
			return R.drawable.heavy_rain;
		}
		else {
			return R.drawable.error_weather;
		}
	}
	
	static public String getPM25Level ( int pm25 ) {
		if ( pm25 < 50 && pm25 > 0 ) {
			return "空气良好";
		} else if ( pm25 < 80 ) {
			return "轻度污染";
		} else {
			return "重度污染";
		}
	}
	
	static public int getWindLevelByString ( String windStr ) {
		int index = 0;
		for (String one : Const.windLevelByString) {
			Pattern pattern = Pattern.compile(one);
			Matcher matcher = pattern.matcher(windStr);
			if ( matcher.find() ) {
				break;
			} else {
				index++;
			}
		}
		return index;
	}
	
	static public String getWindStringByString ( String windStr ) {
		for (String one : Const.windLevelByString) {
			Pattern pattern = Pattern.compile(one);
			Matcher matcher = pattern.matcher(windStr);
			if ( matcher.find() ) {
				return one;
			}
		}
		return "未知";
	}
}
