package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Mpa getMpaById(Integer id) {

        String sql = "SELECT *\n" +
                "            FROM MPA\n" +
                "            WHERE id = ?";

        return jdbc.query(sql, new MpaRowMapper(), id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MPA с id " + id + " не найден"));
    }

    @Override
    public List<Mpa> getAllMpa() {

        String sql = "SELECT *\n" +
                "            FROM MPA";

        return jdbc.query(sql, new MpaRowMapper());
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM MPA WHERE id = ?";

        Integer count = jdbc.queryForObject(sql, Integer.class, id);

        return count != null && count > 0;
    }
}
