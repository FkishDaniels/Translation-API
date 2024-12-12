package ru.kpfu.translationapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class ApplicationConfig {


    @Bean
    public RestTemplate restTemplate(List<HttpMessageConverter<?>> converters) {
        return new RestTemplate(converters);
    }

    @Bean
    public Integer translateThreadCount(@Value("${thread.count}") Integer countThread){
        if (countThread > 10) countThread = 10;

        return countThread;
    }

}
