package ru.kpfu.translationapi.exceptions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AvailableLanguageException extends Exception{
    public AvailableLanguageException() {
        super();
    }
}
