package ru.kpfu.translationapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kpfu.translationapi.config.ApiProperties;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final ApiProperties apiProperties;

}
