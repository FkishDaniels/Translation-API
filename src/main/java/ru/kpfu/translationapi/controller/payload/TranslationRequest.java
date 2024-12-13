package ru.kpfu.translationapi.controller.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TranslationRequest(
        @JsonProperty("sourceLanguageCode")
        @NotBlank(message = "source language shouldn't be a blank")
        @Size(min = 2, max = 7, message = "Invalid size of source language code")
        String sourceLanguage,

        @JsonProperty("targetLanguageCode")
        @NotBlank(message = "target language shouldn't be a blank")
        @Size(min = 2, max = 7, message = "Invalid size of target language code")
        String targetLanguage,

        @JsonProperty("text")
        @NotBlank(message = "{translation.request.text.is_blank}")
        String text
) {
}
