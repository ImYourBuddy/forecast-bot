package com.github.imyourbuddy.service;

import com.github.imyourbuddy.dto.CurrentWeather;
import com.github.imyourbuddy.util.TimeUtil;
import com.github.imyourbuddy.view.CurrentWeatherView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherService {
    @Value("${forecast.bot.api.key}")
    private String apiKey;
    @Autowired
    private WebClient webClient;
    public static final String OPEN_WEATHER_V3_API = "api.openweathermap.org/data/3.0/onecall";

    //TODO: add validation, value can be null
    public CurrentWeatherView getCurrentWeather(double lat, double lon) {
        Mono<CurrentWeather> currentWeatherMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(OPEN_WEATHER_V3_API)
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .queryParam("exclude", "minutely,hourly,daily,alerts")
                        .queryParam("units", "metric")
                        .queryParam("lang", "ru")
                        .build())
                .retrieve()
                .bodyToMono(CurrentWeather.class);
        return currentWeatherMono.map(this::mapToView)
                .block();
    }

    private CurrentWeatherView mapToView(CurrentWeather currentWeather) {
        CurrentWeather.WeatherInfo weatherInfo = currentWeather.getWeatherInfo();
        int temperature = (int) Math.round(weatherInfo.getTemperature());
        int feelsLike = (int) Math.round(weatherInfo.getFeelsLike());
        String sunriseTime = TimeUtil.convertTimestampToTime(weatherInfo.getSunriseTime(), currentWeather.getTimezoneOffset());
        String sunsetTime = TimeUtil.convertTimestampToTime(weatherInfo.getSunsetTime(), currentWeather.getTimezoneOffset());
        double rainProbability = weatherInfo.getRain() == null ? 0 : weatherInfo.getRain().getProbability();
        double snowProbability = weatherInfo.getSnow() == null ? 0 : weatherInfo.getSnow().getProbability();
        return new CurrentWeatherView(
                temperature,
                //TODO: need to handle multiple weather value
                weatherInfo.getWeather().getFirst().getDescription(),
                feelsLike,
                sunriseTime,
                sunsetTime,
                weatherInfo.getWindSpeed(),
                weatherInfo.getClouds(),
                rainProbability,
                snowProbability

        );
    }
}
