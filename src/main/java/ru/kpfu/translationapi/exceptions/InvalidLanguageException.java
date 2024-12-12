package ru.kpfu.translationapi.exceptions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class InvalidLanguageException extends Exception {

    public InvalidLanguageException(String message) {
        super(message);
    }
}
