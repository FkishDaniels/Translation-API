package ru.kpfu.translationapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableConfigurationProperties
public class TranslationApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranslationApiApplication.class, args);
    }

}
