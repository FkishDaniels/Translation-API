package ru.kpfu.translationapi.exceptions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class ServiceException extends Exception{
    public ServiceException() {
    }
}
