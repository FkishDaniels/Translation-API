package ru.kpfu.translationapi.yandex.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TranslationResponse(
        @JsonProperty("translations")
        List<Translation> translations
) {
}
