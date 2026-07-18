package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidateException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT m.id,\n" +
                "                   m.name,\n" +
                "                   m.releaseDate,\n" +
                "                   m.duration,\n" +
                "                   m.description,\n" +
                "                   m.mpa_id,\n" +
                "                   mpa.name AS mpa_name\n" +
                "            FROM movies m\n" +
                "            LEFT JOIN MPA mpa ON m.mpa_id = mpa.id";
        return jdbc.query(sql, new FilmRowMapper());
    }

    @Override
    public Film addNewFilm(Film newFilm) {
        String sql = "INSERT INTO movies (name, releaseDate, duration, description, mpa_id)\n" +
                "            VALUES (?, ?, ?, ?, ?)";

        filmValidation(newFilm);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, newFilm.getName());
            ps.setDate(2, Date.valueOf(newFilm.getReleaseDate()));
            ps.setInt(3, newFilm.getDuration());
            ps.setString(4, newFilm.getDescription());
            ps.setInt(5, newFilm.getMpa().getId());

            return ps;
        }, keyHolder);

        newFilm.setId(keyHolder.getKey().longValue());

        return newFilm;
    }

    @Override
    public Film getFilmById(Long id) {

        String sql = "SELECT m.id,\n" +
                "                   m.name,\n" +
                "                   m.releaseDate,\n" +
                "                   m.duration,\n" +
                "                   m.description,\n" +
                "                   m.mpa_id,\n" +
                "                   mpa.name AS mpa_name\n" +
                "            FROM movies m\n" +
                "            LEFT JOIN MPA mpa ON m.mpa_id = mpa.id\n" +
                "            WHERE m.id = ?";

        return jdbc.query(sql, new FilmRowMapper(), id).stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Фильм не найден"));
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE movies\n" +
                "            SET name = ?, releaseDate = ?, duration = ?, description = ?, mpa_id = ?\n" +
                "            WHERE id = ?";
        getFilmById(film.getId());

        filmValidation(film);

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, film.getName());
            ps.setDate(2, Date.valueOf(film.getReleaseDate()));
            ps.setInt(3, film.getDuration());
            ps.setString(4, film.getDescription());
            ps.setInt(5, film.getMpa().getId());
            ps.setLong(6, film.getId());

            return ps;
        });

        return film;
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

    private Set<Genre> getGenresByFilmId(Long filmId) {

        String sql = "SELECT g.id, g.name\n" +
                "            FROM genres g\n" +
                "            JOIN movie_genres mg ON g.id = mg.genre_id\n" +
                "            WHERE mg.movie_id = ?";

        return new HashSet<>(
                jdbc.query(sql, new GenreRowMapper(), filmId)
        );
    }

    @Override
    public List<Film> getPopularFilms(int count) {

        String sql = "SELECT m.id,\n" +
                "               m.name,\n" +
                "               m.releaseDate,\n" +
                "               m.duration,\n" +
                "               m.description,\n" +
                "               m.mpa_id,\n" +
                "               mpa.name AS mpa_name\n" +
                "        FROM movies m\n" +
                "        LEFT JOIN likes l ON m.id = l.movie_id\n" +
                "        LEFT JOIN MPA mpa ON m.mpa_id = mpa.id\n" +
                "        GROUP BY m.id\n" +
                "        ORDER BY COUNT(l.user_id) DESC\n" +
                "        LIMIT ?";

        return jdbc.query(sql, new FilmRowMapper(), count);
    }

    @Override
    public Set<Long> getLikes(Long filmId) {

        String sql = "SELECT user_id\n" +
                "            FROM likes\n" +
                "            WHERE movie_id = ?";

        return new HashSet<>(jdbc.query(
                        sql,
                        (rs, rowNum) -> rs.getLong("user_id"),
                        filmId
                )
        );
    }

    public boolean existsById(Long filmId) {
        String sql = "SELECT COUNT(*) FROM movies WHERE id = ?";

        Integer count = jdbc.queryForObject(sql, Integer.class, filmId);

        return count != null && count > 0;
    }
}
