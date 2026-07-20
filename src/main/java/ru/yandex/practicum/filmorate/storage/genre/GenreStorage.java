package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    public boolean existsById(int id);

    void addGenres(Long id, Set<Genre> genres);

    Set<Genre> getGenresByFilmId(Long filmId);

    Genre getGenreById(Integer id);

    List<Genre> getAllGenres();

    boolean existAllByIds(Set<Integer> genreIds);
}
