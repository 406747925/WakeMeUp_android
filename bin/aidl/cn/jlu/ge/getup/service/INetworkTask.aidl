package cn.jlu.ge.getup.service;

interface INetworkTask
{
	
	String getFiveDaysWeather (String weatherCity);
	void getFiveDaysWeatherFromNet (String weatherCity);
}