package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public class UserDbStorage implements UserStorage {




    @Override
    public Collection<User> getAllUsers() {
        return List.of();
    }

    @Override
    public User addUser(User newUser) {
        return null;
    }

    @Override
    public User getUserById(Integer id) {
        return null;
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
