package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    public Collection<Film> getFilms();

    public Film addNewFilm(Film newFilm);

    public Film getFilmById(Long id);

    public Film updateFilm(Film newFilm);

    public void filmValidation(Film film);

    boolean existsById(Long filmId);

    List<Film> getPopularFilms(int count);

    Set<Long> getLikes(Long filmId);
}