package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    public Collection<Film> getFilms();

    public Film addNewFilm(@Valid @RequestBody Film newFilm);

    public Film updateFilm(@RequestBody Film newFilm);
}