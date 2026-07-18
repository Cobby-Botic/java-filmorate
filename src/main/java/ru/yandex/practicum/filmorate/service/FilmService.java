package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

@Service
public class FilmService {

    private final JdbcTemplate jdbc;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(JdbcTemplate jdbc, FilmStorage filmStorage, UserStorage userStorage, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbc = jdbc;
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
        String sql = "INSERT INTO likes(movie_id, user_id)\n" +
                "            VALUES (?, ?)";
            jdbc.update(sql, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM likes\n" +
                "            WHERE movie_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count < 0) {
            throw new ConditionsNotMetException("Количество не может быть отрицательным");
        }

        List<Film> films = filmStorage.getPopularFilms(count);

        for (Film film : films) {
            film.setGenres(
                    genreStorage.getGenresByFilmId(film.getId())
            );

            film.setLikes(
                    filmStorage.getLikes(film.getId())
            );
        }
        return films;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);

        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));

        film.setLikes(filmStorage.getLikes(film.getId()));

        return film;
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