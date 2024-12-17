package ru.kpfu.translationapi.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kpfu.translationapi.domain.repository.TranslationDAO;
import ru.kpfu.translationapi.exceptions.AvailableLanguageException;
import ru.kpfu.translationapi.exceptions.InvalidLanguageException;
import ru.kpfu.translationapi.exceptions.ServiceException;
import ru.kpfu.translationapi.exceptions.SymbolsLimitException;
import ru.kpfu.translationapi.yandex.YandexRestClient;
import ru.kpfu.translationapi.yandex.payload.Language;
import ru.kpfu.translationapi.yandex.payload.Translation;


import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    private final TranslationDAO translationDAO;
    private final YandexRestClient restClient;

    @Value("${api.symbols-limit}")
    private Integer symbolsLimit;

    private Set<String> availableLanguages;
    private final Semaphore semaphore = new Semaphore(10); // Ограничение на 10 потоков

    @PostConstruct
    private void init() throws AvailableLanguageException {
        try {
            logger.info("Initializing available languages...");
            this.availableLanguages = this.restClient.getAvailableLanguages()
                    .stream()
                    .map(Language::code)
                    .collect(Collectors.toSet());
            if (this.availableLanguages.isEmpty()) {
                logger.error("No available languages found.");
                throw new AvailableLanguageException();
            }
            logger.info("Available languages initialized successfully.");
        } catch (Exception e) {
            logger.error("Error initializing available languages", e);
            throw e;
        }
    }

    public Translation translate(String sourceLanguageCode,
                                 String targetLanguageCode, String sourceText)
            throws InvalidLanguageException, SymbolsLimitException, ServiceException {
        try {
            logger.info("Starting translation from {} to {}", sourceLanguageCode, targetLanguageCode);

            // Попытка получить разрешение
            semaphore.acquire();
            if (!availableLanguages.contains(sourceLanguageCode)) {
                logger.error("Invalid source language: {}", sourceLanguageCode);
                throw new InvalidLanguageException(sourceLanguageCode);
            } else if (!availableLanguages.contains(targetLanguageCode)) {
                logger.error("Invalid target language: {}", targetLanguageCode);
                throw new InvalidLanguageException(targetLanguageCode);
            }

            var words = parseWords(sourceText);
            var results = getTranslations(words, sourceLanguageCode, targetLanguageCode);

            String translatedText = String.join(" ", results);
            saveTranslation(sourceLanguageCode, targetLanguageCode, sourceText, translatedText);

            logger.info("Translation completed successfully.");
            return new Translation(translatedText);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Translation process interrupted.", e);
            throw new ServiceException();
        } finally {
            semaphore.release();
        }
    }

    private synchronized void saveTranslation(String sourceLanguageCode, String targetLanguageCode, String sourceText, String translatedText) {
        logger.debug("Saving translation from {} to {}: {}", sourceLanguageCode, targetLanguageCode, translatedText);
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
        logger.debug("Parsing source text: {}", text);
        var words = Arrays.stream(text.split("\\s+"))
                .map(String::trim)
                .filter(trim -> !trim.isEmpty())
                .toArray(String[]::new);

        int wordsLen = Arrays.stream(words)
                .mapToInt(String::length)
                .sum();

        if (symbolsLimit <= wordsLen) {
            logger.error("Text exceeds symbol limit: {} characters", wordsLen);
            throw new SymbolsLimitException();
        }

        logger.debug("Parsed words: {}", Arrays.toString(words));
        return words;
    }

    private String[] getTranslations(String[] words, String sourceLanguageCode, String targetLanguageCode) throws ServiceException {
        logger.debug("Getting translations for {} words", words.length);
        var results = new String[words.length];
        int index = 0;
        for (String word : words) {
            var result = safeTranslateText(sourceLanguageCode, targetLanguageCode, word);
            results[index] = result;
            index++;
        }
        if (Arrays.stream(results).anyMatch(Objects::isNull)) {
            logger.error("Translation failed for some words.");
            throw new ServiceException();
        }
        logger.debug("Translations completed.");
        return results;
    }

    private synchronized String safeTranslateText(String sourceLanguageCode, String targetLanguageCode, String word) {
        logger.debug("Translating word: {} from {} to {}", word, sourceLanguageCode, targetLanguageCode);
        return this.restClient.translateText(sourceLanguageCode, targetLanguageCode, word);
    }
}