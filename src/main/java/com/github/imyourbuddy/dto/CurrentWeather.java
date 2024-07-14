package com.github.imyourbuddy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class CurrentWeather {
    private double lat;
    private double lon;
    private String timezone;
    @JsonProperty("timezone_offset")
    private int timezoneOffset;
    @JsonProperty("current")
    private WeatherInfo weatherInfo;

    @Getter
    public static class WeatherInfo {
        @JsonProperty("dt")
        private long currentTime;
        @JsonProperty("sunrise")
        private long sunriseTime;
        @JsonProperty("sunset")
        private long sunsetTime;
        @JsonProperty("temp")
        private double temperature;
        @JsonProperty("feels_like")
        private double feelsLike;
        private int pressure;
        private int humidity;
        @JsonProperty("dew_point")
        private double atmosphericTemperature;
        @JsonProperty("uvi")
        private double uvIndex;
        private int clouds;
        private int visibility;
        @JsonProperty("wind_speed")
        private double windSpeed;
        @JsonProperty("wind_deg")
        private int windDirection;
        @JsonProperty("wind_gust")
        private double windGust;
        private List<WeatherData> weather;
        private RainInfo rain;
        private SnowInfo snow;
    }
}
