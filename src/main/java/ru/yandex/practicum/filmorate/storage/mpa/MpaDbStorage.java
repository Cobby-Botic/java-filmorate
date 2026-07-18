package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM MPA WHERE id = ?";

        Integer count = jdbc.queryForObject(sql, Integer.class, id);

        return count != null && count > 0;
    }
}
