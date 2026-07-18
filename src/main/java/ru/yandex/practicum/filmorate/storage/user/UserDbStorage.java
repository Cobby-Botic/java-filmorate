package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
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
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO FriendShips (user_id, friend_id, status) VALUES (?, ?, ?)";
        if (!userExists(userId)) {
            throw new NotFoundException("Пользователя с id " + userId + " не существует");
        }

        if (!userExists(friendId)) {
            throw new NotFoundException("Пользователя с id " + friendId + " не существует");
        }

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            ps.setString(3, FriendshipStatus.PENDING.name()); // сохраняем имя статуса как строку
            return ps;
        });
    }

    @Override
    public List<User> getFriendsByUserId(Long id) {
        if (!userExists(id)) {
            throw new NotFoundException("Пользователя с id " + id + " не существует");
        }

        String sql = "SELECT u.*\n" +
                "FROM FRIENDSHIPS f\n" +
                "JOIN USERS u ON u.id = f.friend_id\n" +
                "WHERE f.user_id = " + id;
        return jdbc.query(sql, new UserRowMapper());
    }

    @Override
    public void deleteFriends(Long id, Long friendId) {
        String sql = "DELETE FROM FriendShips WHERE user_id = ? AND friend_id = ?";


        if (!userExists(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (!userExists(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }

        jdbc.update(sql, id, friendId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        String sql = "SELECT DISTINCT u.* " +
                "FROM USERS u " +
                "JOIN FRIENDSHIPS f1 ON u.ID = f1.FRIEND_ID AND f1.USER_ID IN (?, ?) " +
                "JOIN FRIENDSHIPS f2 ON u.ID = f2.FRIEND_ID AND f2.USER_ID IN (?, ?) " +
                "WHERE f1.USER_ID <> f2.USER_ID";

        return jdbc.query(sql, new Object[]{userId, friendId, userId, friendId}, new UserRowMapper());
    }

    @Override
    public boolean userExists(Long userId) {
        String sql = "SELECT COUNT(*) FROM Users WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }
}
