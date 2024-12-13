package ru.kpfu.translationapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.translationapi.controller.payload.TranslationRequest;
import ru.kpfu.translationapi.exceptions.InvalidLanguageException;
import ru.kpfu.translationapi.exceptions.ServiceException;
import ru.kpfu.translationapi.exceptions.SymbolsLimitException;
import ru.kpfu.translationapi.service.TranslationService;
import ru.kpfu.translationapi.yandex.payload.Translation;

@RequiredArgsConstructor
@RestController
@RequestMapping("/translation")

public class TranslationController {
    private final TranslationService translationService;

    @PostMapping()
    public Translation translate(
            @Valid @RequestBody TranslationRequest translationRequest
    ) throws SymbolsLimitException, ServiceException, InvalidLanguageException
    {
        return translationService.translate(translationRequest.sourceLanguage(),
                translationRequest.targetLanguage(),translationRequest.text());
    }
}
