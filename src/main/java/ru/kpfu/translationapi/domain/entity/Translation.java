package ru.kpfu.translationapi.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Translation {
    private Long id;
    private String sourceLanguage;
    private String targetLanguage;
    private String sourceText;
    private String translatedText;
}
