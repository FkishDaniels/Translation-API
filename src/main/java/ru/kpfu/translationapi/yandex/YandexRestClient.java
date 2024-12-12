package ru.kpfu.translationapi.yandex;

import ru.kpfu.translationapi.yandex.payload.Language;

import java.util.List;

public interface YandexRestClient {
    String translateText(String sourceLanguage, String targetLanguage, String text);
    List<Language> getAvailableLanguages();
}
