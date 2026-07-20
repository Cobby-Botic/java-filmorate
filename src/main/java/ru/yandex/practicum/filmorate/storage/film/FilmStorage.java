package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {

    public Collection<Film> getFilms();

    public Film addNewFilm(Film newFilm);

    public Film getFilmById(Long id);

    public Film updateFilm(Film newFilm);

    public void filmValidation(Film film);

    List<Film> getPopularFilms(int count);

    public void addLike(Long filmId, Long userId);

    public void deleteLike(Long filmId, Long userId);

    public boolean existsById(Long filmId);
}