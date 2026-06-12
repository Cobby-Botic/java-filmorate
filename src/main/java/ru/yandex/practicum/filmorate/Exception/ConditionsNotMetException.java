package ru.yandex.practicum.filmorate.Exception;

public class ConditionsNotMetException extends NullPointerException {
    public ConditionsNotMetException(String message) {
        super(message);
    }
}
