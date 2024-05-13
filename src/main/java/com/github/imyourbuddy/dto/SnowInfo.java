package com.github.imyourbuddy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SnowInfo {
    @JsonProperty("1h")
    private double probability;
}
