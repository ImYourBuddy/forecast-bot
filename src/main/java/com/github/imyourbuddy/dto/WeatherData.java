package com.github.imyourbuddy.dto;

import lombok.Getter;

@Getter
public class WeatherData {
    private int id;
    private String main;
    private String description;
    private String icon;
}
