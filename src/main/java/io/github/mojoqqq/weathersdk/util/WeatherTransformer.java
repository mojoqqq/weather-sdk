package io.github.mojoqqq.weathersdk.util;

import io.github.mojoqqq.weathersdk.model.OpenWeatherRs;
import io.github.mojoqqq.weathersdk.model.WeatherData;

public class WeatherTransformer {
    public static WeatherData transform(OpenWeatherRs original) {
        WeatherData transformed = new WeatherData();

        // Преобразуем weather
        if (original.getWeather() != null && !original.getWeather().isEmpty()) {
            OpenWeatherRs.Weather originalWeather = original.getWeather().get(0);
            WeatherData.Weather weather = new WeatherData.Weather();
            weather.setMain(originalWeather.getMain());
            weather.setDescription(originalWeather.getDescription());
            transformed.setWeather(weather);
        }

        // Преобразуем temperature
        if (original.getMain() != null) {
            WeatherData.Temperature temperature = new WeatherData.Temperature();
            temperature.setTemp(original.getMain().getTemp());
            temperature.setFeelsLike(original.getMain().getFeelsLike());
            transformed.setTemperature(temperature);
        }

        // Преобразуем visibility
        transformed.setVisibility(original.getVisibility());

        // Преобразуем wind
        if (original.getWind() != null) {
            WeatherData.Wind wind = new WeatherData.Wind();
            wind.setSpeed(original.getWind().getSpeed());
            transformed.setWind(wind);
        }

        // Преобразуем datetime
        transformed.setDatetime(original.getDatetime());

        // Преобразуем sys
        if (original.getSys() != null) {
            WeatherData.Sys sys = new WeatherData.Sys();
            sys.setSunrise(original.getSys().getSunrise());
            sys.setSunset(original.getSys().getSunset());
            transformed.setSys(sys);
        }

        // Преобразуем timezone и name
        transformed.setTimezone(original.getTimezone());
        transformed.setName(original.getName());

        return transformed;
    }
}