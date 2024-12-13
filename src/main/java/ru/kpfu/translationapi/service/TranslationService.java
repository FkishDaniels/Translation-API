package ru.kpfu.translationapi.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kpfu.translationapi.config.ApiProperties;
import ru.kpfu.translationapi.domain.repository.TranslationDAO;
import ru.kpfu.translationapi.exceptions.AvailableLanguageException;
import ru.kpfu.translationapi.exceptions.InvalidLanguageException;
import ru.kpfu.translationapi.exceptions.ServiceException;
import ru.kpfu.translationapi.exceptions.SymbolsLimitException;
import ru.kpfu.translationapi.yandex.YandexRestClient;
import ru.kpfu.translationapi.yandex.payload.Language;
import ru.kpfu.translationapi.yandex.payload.Translation;

import java.security.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationDAO translationDAO;
    private final YandexRestClient restClient;

    @Value("${api.symbols-limit}")
    private Integer symbolsLimit;
    private Set<String> availableLanguages;

    @PostConstruct
    private void init() throws AvailableLanguageException {

        this.availableLanguages = this.restClient.getAvailableLanguages()
                .stream()
                .map(Language::code)
                .collect(Collectors.toSet());
        if(this.availableLanguages.isEmpty())
            throw  new AvailableLanguageException();
    }

    public Translation translate(String sourceLanguageCode,
                                 String targetLanguageCode, String sourceText)
            
            throws InvalidLanguageException,
            SymbolsLimitException, ServiceException {

        if (!availableLanguages.contains(sourceLanguageCode)) {
            throw new InvalidLanguageException(sourceLanguageCode);
        } else if (!availableLanguages.contains(targetLanguageCode)) {
            throw new InvalidLanguageException(targetLanguageCode);
        }

        var words = parseWords(sourceText);
        var results = getTranslations(words, sourceLanguageCode, targetLanguageCode);

        String translatedText = String.join(" ", results);
        saveTranslation(sourceLanguageCode, targetLanguageCode,
                sourceText, translatedText);

        return new Translation(translatedText);
    }

    private void saveTranslation(String sourceLanguageCode, String targetLanguageCode, String sourceText, String translatedText) {
        translationDAO.save(
                ru.kpfu.translationapi.domain.entity.Translation.builder()
                        .translatedText(translatedText)
                        .sourceText(sourceText)
                        .sourceLanguage(sourceLanguageCode)
                        .targetLanguage(targetLanguageCode)
                        .build()
        );
    }


    private String[] parseWords(String text) throws SymbolsLimitException {
        var words = Arrays.stream(text.split("\\s+"))
                .map(String::trim)
                .filter(trim -> !trim.isEmpty())
                .toArray(String[]::new);

        int wordsLen = Arrays.stream(words)
                .mapToInt(String::length)
                .sum();

        if (symbolsLimit <= wordsLen) {
            throw new SymbolsLimitException();
        }

        return words;
    }

    private String[] getTranslations(String[] words, String sourceLanguageCode, String targetLanguageCode) throws ServiceException {
        var results = new String[words.length];
        int index = 0;
        for(String word: words) {
            var result = this.restClient.translateText(sourceLanguageCode, targetLanguageCode, word);

            results[index] = result;
            index++;
        }
        if (Arrays.stream(results).anyMatch(Objects::isNull)) {
            throw new ServiceException();
        }
        return results;
    }
}
