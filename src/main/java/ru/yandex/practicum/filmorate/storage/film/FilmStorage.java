package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    public Collection<Film> getFilms();

    public Film addNewFilm(Film newFilm);

    public Optional<Film> getFilmById(Integer id);

    public Film updateFilm(Film newFilm);

    public void filmValidation(Film film);
}