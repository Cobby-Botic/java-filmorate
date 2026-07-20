package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidateException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedJdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = """
                SELECT m.id,
                       m.name,
                       m.releaseDate,
                       m.duration,
                       m.description,
                       m.mpa_id,
                       mpa.name AS mpa_name
                FROM movies m
                LEFT JOIN MPA mpa ON m.mpa_id = mpa.id
                """;

        List<Film> films = jdbc.query(sql, new FilmRowMapper());
        fillFilms(films);
        return films;
    }

    @Override
    public Film addNewFilm(Film newFilm) {
        String sql = """
                INSERT INTO movies (name, releaseDate, duration, description, mpa_id)
                VALUES (?, ?, ?, ?, ?)
                """;

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
        String sql = """
                SELECT m.id,
                       m.name,
                       m.releaseDate,
                       m.duration,
                       m.description,
                       m.mpa_id,
                       mpa.name AS mpa_name
                FROM movies m
                LEFT JOIN MPA mpa ON m.mpa_id = mpa.id
                WHERE m.id = ?
                """;

        Film film = jdbc.query(sql, new FilmRowMapper(), id).stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        fillFilms(List.of(film));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = """
                UPDATE movies
                SET name = ?, releaseDate = ?, duration = ?, description = ?, mpa_id = ?
                WHERE id = ?
                """;

        if (!existsById(film.getId())) {
            throw new NotFoundException("Фильм не найден");
        }

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

        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT m.id,\n" +
                "                       m.name,\n" +
                "                       m.releaseDate,\n" +
                "                       m.duration,\n" +
                "                       m.description,\n" +
                "                       m.mpa_id,\n" +
                "                       mpa.name AS mpa_name\n" +
                "                FROM movies m\n" +
                "                LEFT JOIN likes l ON m.id = l.movie_id\n" +
                "                LEFT JOIN MPA mpa ON m.mpa_id = mpa.id\n" +
                "                GROUP BY m.id, m.name, m.releaseDate, m.duration,\n" +
                "                         m.description, m.mpa_id, mpa.name\n" +
                "                ORDER BY COUNT(l.user_id) DESC, m.id\n" +
                "                LIMIT ?";

        List<Film> films = jdbc.query(sql, new FilmRowMapper(), count);
        fillFilms(films);
        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = """
                INSERT INTO likes(movie_id, user_id)
                VALUES (?, ?)
                """;
        jdbc.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = """
                DELETE FROM likes
                WHERE movie_id = ? AND user_id = ?
                """;
        jdbc.update(sql, filmId, userId);
    }

    @Override
    public boolean existsById(Long filmId) {
        String sql = "SELECT COUNT(*) FROM movies WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, filmId);
        return count != null && count > 0;
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

    private void fillFilms(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        Map<Long, Set<Genre>> genresByFilm = loadGenres(filmIds);
        Map<Long, Set<Long>> likesByFilm = loadLikes(filmIds);

        for (Film film : films) {
            film.setGenres(genresByFilm.getOrDefault(film.getId(), new LinkedHashSet<>()));

            film.setLikes(likesByFilm.getOrDefault(film.getId(), new HashSet<>()));
        }
    }

    private Map<Long, Set<Genre>> loadGenres(List<Long> filmIds) {
        String sql = """
            SELECT mg.movie_id,
                   g.id,
                   g.name
            FROM movie_genres mg
            JOIN genres g ON g.id = mg.genre_id
            WHERE mg.movie_id IN (:ids)
            ORDER BY mg.movie_id, g.id
            """;

        Map<Long, Set<Genre>> result = new HashMap<>();
        MapSqlParameterSource params =
                new MapSqlParameterSource("ids", filmIds);

        namedJdbc.query(sql, params, rs -> {
            Long filmId = rs.getLong("movie_id");

            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));

            result.computeIfAbsent(
                    filmId,
                    key -> new LinkedHashSet<>()
            ).add(genre);
        });

        return result;
    }

    private Map<Long, Set<Long>> loadLikes(List<Long> filmIds) {
        String sql = """
                SELECT movie_id,
                       user_id
                FROM likes
                WHERE movie_id IN (:ids)
                """;

        Map<Long, Set<Long>> result = new HashMap<>();
        MapSqlParameterSource params = new MapSqlParameterSource("ids", filmIds);

        namedJdbc.query(sql, params, rs -> {
            Long filmId = rs.getLong("movie_id");
            Long userId = rs.getLong("user_id");

            result.computeIfAbsent(filmId, key -> new HashSet<>()).add(userId);
        });

        return result;
    }
}