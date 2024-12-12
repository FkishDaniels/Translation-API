package ru.kpfu.translationapi.yandex.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Translation(
        @JsonProperty("text")
        String text
) {
}
