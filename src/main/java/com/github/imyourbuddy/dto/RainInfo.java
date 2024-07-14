package com.github.imyourbuddy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RainInfo {
    @JsonProperty("1h")
    private double probability;
}
