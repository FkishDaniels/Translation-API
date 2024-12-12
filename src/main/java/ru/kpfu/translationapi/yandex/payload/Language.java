package ru.kpfu.translationapi.yandex.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Language(
        @JsonProperty("code")
        String code
) { }
