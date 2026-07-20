package ru.yandex.practicum.filmorate.Exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}