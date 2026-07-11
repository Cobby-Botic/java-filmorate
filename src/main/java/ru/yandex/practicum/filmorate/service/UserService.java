package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
        return userStorage.getUserById(userId);
    }

    public List<User> getFriendsByUserId(Integer id) {
        return userStorage.getFriendsByUserId(id);
    }

    public void deleteFriendByUser(Integer id, Integer friendId) {
        userStorage.deleteFriends(id, friendId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }
}
