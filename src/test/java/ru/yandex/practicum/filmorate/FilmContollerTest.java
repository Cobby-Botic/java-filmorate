package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    @Test
    void addNewFilm_shouldAddFilm_whenDataIsCorrect() {
        FilmController controller = new FilmController();

        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Good film");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);

        Film savedFilm = controller.addNewFilm(film);

        assertNotNull(savedFilm.getId());
        assertEquals("Matrix", savedFilm.getName());
        assertEquals(1, controller.getFilms().size());
    }

    @Test
    void addNewFilm_shouldThrowException_whenNameIsEmpty() {
        FilmController controller = new FilmController();

        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> controller.addNewFilm(film)
        );

        assertEquals(
                "Название фильма не может быть пустым",
                exception.getMessage()
        );
    }

    @Test
    void addNewFilm_shouldThrowException_whenDescriptionTooLong() {
        FilmController controller = new FilmController();

        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> controller.addNewFilm(film)
        );

        assertEquals(
                "Длина описания не может быть больше 200 символов",
                exception.getMessage()
        );
    }

    @Test
    void addNewFilm_shouldThrowException_whenReleaseDateTooEarly() {
        FilmController controller = new FilmController();

        Film film = new Film();
        film.setName("Old film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(10);

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> controller.addNewFilm(film)
        );

        assertEquals(
                "Дата релиза не может быть раньше 28 декабря 1895 года",
                exception.getMessage()
        );
    }

    @Test
    void addNewFilm_shouldThrowException_whenDurationIsNegative() {
        FilmController controller = new FilmController();

        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(-1);

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> controller.addNewFilm(film)
        );

        assertEquals(
                "Продолжительность фильма должна быть положительным числом",
                exception.getMessage()
        );
    }
}
