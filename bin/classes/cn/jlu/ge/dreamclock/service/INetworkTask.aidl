package cn.jlu.ge.dreamclock.service;

interface INetworkTask
{
	
	String getFiveDaysWeather (String weatherCity);
	void getFiveDaysWeatherFromNet (String weatherCity);
}