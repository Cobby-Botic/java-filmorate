package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc,
                          NamedParameterJdbcTemplate namedJdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM genres WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void addGenres(Long filmId, Set<Genre> genres) {

        if (genres == null || genres.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO movie_genres(movie_id, genre_id)\n" +
                "            VALUES (?, ?)";

        for (Genre genre : genres) {
            jdbc.update(sql, filmId, genre.getId());
        }
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long filmId) {

        String sql = "SELECT g.id,\n" +
                "                   g.name\n" +
                "            FROM genres g\n" +
                "            JOIN movie_genres mg\n" +
                "                 ON g.id = mg.genre_id\n" +
                "            WHERE mg.movie_id = ?\n" +
                "            ORDER BY g.id";

        return new HashSet<>(jdbc.query(sql, new GenreRowMapper(), filmId));
    }

    @Override
    public Genre getGenreById(Integer id) {

        String sql = "SELECT *\n" +
                "            FROM genres\n" +
                "            WHERE id = ?";

        return jdbc.query(sql, new GenreRowMapper(), id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
    }

    @Override
    public List<Genre> getAllGenres() {

        String sql = "SELECT *\n" +
                "            FROM genres\n" +
                "            ORDER BY id";

        return jdbc.query(sql, new GenreRowMapper());
    }

    @Override
    public boolean existAllByIds(Set<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return true;
        }

        String sql = "            SELECT COUNT(DISTINCT id)\n" +
                "            FROM genres\n" +
                "            WHERE id IN (:ids)";

        MapSqlParameterSource params =
                new MapSqlParameterSource("ids", genreIds);

        Integer count = namedJdbc.queryForObject(sql, params, Integer.class);

        return count != null && count == genreIds.size();
    }
}
