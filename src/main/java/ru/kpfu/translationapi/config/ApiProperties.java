package ru.kpfu.translationapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("api")
public record ApiProperties(
        String url,
        String api
) {
}
