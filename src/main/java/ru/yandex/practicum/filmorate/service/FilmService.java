package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public void like(Long filmId, Long userId) {
       return;
    }

    public void deleteLike(Integer id, Integer userId) {
        return;
    }

    public List<Film> getPopularFilms(int count) {
       return List.of();
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film updateFilm(Film newFilm) {
        if (!mpaStorage.existsById(newFilm.getMpa().getId())) {
            throw new NotFoundException("Такого MPA не существует");
        }
        return filmStorage.updateFilm(newFilm);
    }

    public Film addNewFilm(Film newFilm) {

        if (!mpaStorage.existsById(newFilm.getMpa().getId())) {
            throw new NotFoundException("Такого MPA не существует");
        }

        validateGenres(newFilm);

        Film film = filmStorage.addNewFilm(newFilm);

        genreStorage.addGenres(film.getId(), film.getGenres());

        return film;
    }

    private void validateGenres(Film film) {

        if (film.getGenres() == null) {
            return;
        }

        for (Genre genre : film.getGenres()) {
            if (!genreStorage.existsById(genre.getId())) {
                throw new NotFoundException("Жанра с id " + genre.getId() + " не существует");
            }
        }
    }
}