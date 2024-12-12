package ru.kpfu.translationapi.yandex;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.kpfu.translationapi.config.ApiProperties;
import ru.kpfu.translationapi.yandex.payload.AvailableLanguageResponse;
import ru.kpfu.translationapi.yandex.payload.Language;
import ru.kpfu.translationapi.yandex.payload.TranslationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class YandexRestClientImplementation implements YandexRestClient{

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    @Override
    public String translateText(String sourceLanguage, String targetLanguage, String text) {
        var requestBody = new HashMap<>();
        requestBody.put("sourceLanguageCode", sourceLanguage);
        requestBody.put("targetLanguageCode", targetLanguage);
        requestBody.put("texts", new ArrayList<>() {{
            add(text);
        }});
        var headers = new HttpHeaders();
        headers.add("Authorization", "Api-Key " + apiProperties.api());
        var request = new HttpEntity<>(requestBody, headers);

        try {
            var response = this.restTemplate.postForEntity(apiProperties.url() + "/translate", request,
                    TranslationResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null
                    && response.getBody().translations() != null) {
                return response.getBody().translations().getFirst().text();
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusText().equals("Too Many Requests")) {
                throw new RuntimeException(ex.getStatusText());
            } else {
                throw new RuntimeException("Invalid API Key: " + apiProperties.api());
            }
        }

        return null;
    }

    @Override
    public List<Language> getAvailableLanguages() {
        var headers = new HttpHeaders();
        headers.add("Authorization", "Api-Key " + apiProperties.api());
        var request = new HttpEntity<>(headers);

        try {
            var response = this.restTemplate.postForEntity(apiProperties.url() + "/languages", request,
                    AvailableLanguageResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null
                    && response.getBody().languages() != null) {
                return response.getBody().languages();
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusText().equals("Too Many Requests")) {
                throw new RuntimeException(ex.getStatusText());
            } else {
                throw new RuntimeException("Invalid API Key: " + apiProperties.api());
            }
        }

        return new ArrayList<>();
    }
}
