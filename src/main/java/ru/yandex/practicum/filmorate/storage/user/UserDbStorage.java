package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, new UserRowMapper());
    }

    @Override
    public User getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbc.query(sql, new UserRowMapper(), id).stream().findAny()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public User addUser(User newUser) {
        String sql = "INSERT INTO users (name, email, login, birthdate) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newUser.getName());
            ps.setString(2, newUser.getEmail());
            ps.setString(3, newUser.getLogin());
            ps.setDate(4, Date.valueOf(newUser.getBirthday()));
            return ps;
        }, keyHolder);
        newUser.setId(keyHolder.getKey().longValue());
        return newUser;
    }

    @Override
    public User updateUser(User newUser) {
        String sql = "UPDATE users SET name = ?, email = ?, login = ?, birthdate = ? WHERE id = ?";
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newUser.getName());
            ps.setString(2, newUser.getEmail());
            ps.setString(3, newUser.getLogin());
            ps.setDate(4, Date.valueOf(newUser.getBirthday()));
            ps.setLong(5, newUser.getId());
            return ps;
        });
        return newUser;
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        return List.of();
    }

    @Override
    public void deleteFriends(Integer id, Integer friendId) {

    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        return List.of();
    }
}
