package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface GenreStorage {

    public boolean existsById(int id);

    void addGenres(Long id, Set<Genre> genres);
}
