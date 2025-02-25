package io.github.mojoqqq.weathersdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class WeatherData {
    @JsonProperty("weather")
    private Weather weather;

    @JsonProperty("temperature")
    private Temperature temperature;

    @JsonProperty("visibility")
    private int visibility;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("datetime")
    private long datetime;

    @JsonProperty("sys")
    private Sys sys;

    @JsonProperty("timezone")
    private int timezone;

    @JsonProperty("name")
    private String name;

    @Data
    public static class Weather {
        @JsonProperty("main")
        private String main;

        @JsonProperty("description")
        private String description;

    }
    @Data
    public static class Temperature {
        @JsonProperty("temp")
        private double temp;

        @JsonProperty("feels_like")
        private double feelsLike;

    }
    @Data
    public static class Wind {
        @JsonProperty("speed")
        private double speed;
    }
@Data
    public static class Sys {
        @JsonProperty("sunrise")
        private long sunrise;

        @JsonProperty("sunset")
        private long sunset;

    }
}