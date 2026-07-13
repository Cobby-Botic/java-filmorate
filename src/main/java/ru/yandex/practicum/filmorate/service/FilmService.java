package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void like(Integer filmId, Integer userId) {
        Optional<Film> optFilm = filmStorage.getFilmById(filmId);
        if (optFilm.isPresent()) {
            Film film = optFilm.get();
            User user = userStorage.getUserById(userId);
            film.getLikes().add(user.getId());
        }
        throw new NotFoundException("Фильм с id " + filmId + " не найден");
    }

    public void deleteLike(Integer id, Integer userId) {
        Optional<Film> optFilm = filmStorage.getFilmById(id);
        if (optFilm.isPresent()) {
          Film film = optFilm.get();
            User user = userStorage.getUserById(userId);
            if (film.getLikes().contains(userId)) {
                film.getLikes().remove(userId);
                return;
            }
            throw new NotFoundException("Пользователь не ставил лайк на этот фильм");
        }
    }

    public List<Film> getPopularFilms(int count) {
        if (count > 0) {
            return filmStorage.getFilms().stream().sorted(
                            (film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                    .limit(count)
                    .collect(Collectors.toList());
        }
        throw new ValidateException("Количество фильмов не может быть отрицательным!");
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        Optional<Film> film = filmStorage.getFilmById(id);
        if (film.isPresent()) {
            return film.get();
        }
        throw new NotFoundException("Фильм с id " + id + " не найден");
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film addNewFilm(Film newFilm) {
        return filmStorage.addNewFilm(newFilm);
    }
}
