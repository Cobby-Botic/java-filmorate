package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public void like(Long filmId, Long userId) {
        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException("Фильм не найден");
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (!filmStorage.existsById(filmId.longValue())) {
            throw new NotFoundException("Фильм не найден");
        }

        if (!userStorage.userExists(userId.longValue())) {
            throw new NotFoundException("Пользователь не найден");
        }

        filmStorage.deleteLike(filmId.longValue(), userId.longValue());
    }

    public List<Film> getPopularFilms(int count) {
        if (count < 0) {
            throw new ConditionsNotMetException("Количество не может быть отрицательным");
        }

        return filmStorage.getPopularFilms(count);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film updateFilm(Film newFilm) {
        validateMpa(newFilm);
        validateGenres(newFilm);
        return filmStorage.updateFilm(newFilm);
    }

    public Film addNewFilm(Film newFilm) {
        validateMpa(newFilm);
        validateGenres(newFilm);

        Film film = filmStorage.addNewFilm(newFilm);
        genreStorage.addGenres(film.getId(), film.getGenres());

        return filmStorage.getFilmById(film.getId());
    }

    private void validateMpa(Film film) {
        if (film.getMpa() == null || !mpaStorage.existsById(film.getMpa().getId())) {
            throw new NotFoundException("Такого MPA не существует");
        }
    }

    private void validateGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        Set<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        if (!genreStorage.existAllByIds(genreIds)) {
            throw new NotFoundException("Один или несколько жанров не существуют");
        }
    }
}