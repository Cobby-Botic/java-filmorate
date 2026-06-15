package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> filmMap = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Запрос всех фильмов");
        return filmMap.values();
    }

    @PostMapping
    public Film addNewFilm(@Valid @RequestBody Film newFilm) {
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза раньше минимальной");
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
        } //оставил проверку на даты (пока что)
        newFilm.setId(getNextId());
        filmMap.put(newFilm.getId(), newFilm);
         return newFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("Запрос на обновление данных фильма");
        if (newFilm.getId() == null) {
            log.warn("id - пустое");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (filmMap.containsKey(newFilm.getId())) {
            log.info("фильм с id найден");
            Film oldFilm = filmMap.get(newFilm.getId());
            if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                log.info("Обновление имени фильма");
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank() && newFilm.getDescription().length() <= 200) {
                log.info("Обновление описания");
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getDuration() != null && newFilm.getDuration() > 0) {
                log.info("Обновление продолжительности");
                oldFilm.setDuration(newFilm.getDuration());
            }
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
                log.info("Обновление даты выпуска");
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            return oldFilm;
        }
        log.warn("Фильм с id в запросе не найден");
        throw new ConditionsNotMetException("Фильм с id не найден");
    }

    private Integer getNextId() {
        log.info("Генерация следующего ID");
        long currentMaxId = filmMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return Math.toIntExact(++currentMaxId);
    }
}
