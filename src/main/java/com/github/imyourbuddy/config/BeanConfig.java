package com.github.imyourbuddy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import redis.clients.jedis.Jedis;

@Configuration
public class BeanConfig {
    @Bean
    public WebClient restTemplate() {
        return WebClient.create();
    }

    @Bean
    public Jedis jedis(@Value("${redis.host}") String redisHost, @Value("${redis.port}") int redisPort) {
        return new Jedis(redisHost, redisPort);
    }
}
