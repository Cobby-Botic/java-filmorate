package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage storage;

    @GetMapping
    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film newFilm) {
        return storage.addNewFilm(newFilm);
    }
}