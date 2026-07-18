package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    public void addGenres(Long filmId, Set<Genre> genres) {

        String sql = "INSERT INTO movie_genres(movie_id, genre_id)\n" +
                "            VALUES (?, ?)";

        for (Genre genre : genres) {
            jdbc.update(sql, filmId, genre.getId());
        }
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM genres WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
