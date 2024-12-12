package ru.kpfu.translationapi.exceptions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class SymbolsLimitException extends Exception{
    public SymbolsLimitException() {
    }
}
