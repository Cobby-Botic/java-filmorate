package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage storage;
    private final FilmService service;

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Запрос на получение всех фильмов");
        return storage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info("Запрос на получение фильма");
        return storage.getFilmById(id);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("Запрос на изменение данных в фильме");
        return storage.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Пользователь ставит лайк");
        service.like(id, userId);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film newFilm) {
        log.info("Добавление фильма " + newFilm.getName());
        return storage.addNewFilm(newFilm);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Пользователь убирает лайк");
        service.deleteLike(id, userId);
    }
}