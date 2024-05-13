package com.github.imyourbuddy.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@AllArgsConstructor
@Getter
public class CurrentWeatherView implements Serializable {
    private final int temperature;
    private final String weatherDescription;
    private final int feelsLike;
    private final String sunrise;
    private final String sunset;
    private final double windSpeed;
    private final int cloudiness;
    private final double rainProbability;
    private final double snowProbability;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                """
                        В настоящее время температура составляет %d°C, ощущается как %d°C, %s.
                        Восход солнца сегодня в %s, закат в %s.
                        Ветер дует со скоростью %.1f км/ч, облачность составляет %d%%.""",
                temperature,
                feelsLike,
                weatherDescription,
                sunrise,
                sunset,
                windSpeed,
                cloudiness
        ));

        if (rainProbability > 0) {
            sb.append(String.format(" Вероятность дождя %.1f%%.", rainProbability * 100));
        }
        if (snowProbability > 0) {
            sb.append(String.format(" Вероятность снега %.1f%%.", snowProbability * 100));
        }

        return sb.toString();
    }
}
