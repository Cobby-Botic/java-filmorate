package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> filmMap = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return filmMap.values();
    }

    @Override
    public Film addNewFilm(Film newFilm) {
        filmValidation(newFilm);
        newFilm.setId(getNextId());
        newFilm.setLikes(new HashSet<>());
        filmMap.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidateException("Id должен быть указан");
        }
        if (filmMap.containsKey(newFilm.getId())) {
            Film oldFilm = filmMap.get(newFilm.getId());
            if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank() && newFilm.getDescription().length() <= 200) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getDuration() != null && newFilm.getDuration() > 0) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id не найден");
    }

    @Override
    public void filmValidation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))
                || film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ValidateException("Некорректно указана дата релиза.");
        }

        if (film.getName().isEmpty()) {
            throw new ValidateException("Некорректно указано название фильма.");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidateException("Превышено количество символов в описании фильма.");
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        if (filmMap.containsKey(id)) {
            return filmMap.get(id);
        }
        throw new NotFoundException("Фильм с id " + id + " не найден");
    }

    private Integer getNextId() {
        long currentMaxId = filmMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return Math.toIntExact(++currentMaxId);
    }
}
