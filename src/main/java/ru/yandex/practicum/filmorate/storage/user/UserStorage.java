package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    public Collection<User> getAllUsers();

    public User addUser(User newUser);

    public User getUserById(Long id);

    public User updateUser(User newUser);

    public User addFriend(Integer userId, Integer friendId);

    public List<User> getFriendsByUserId(Integer id);

    public void deleteFriends(Integer id, Integer friendId);

    public List<User> getCommonFriends(Integer userId, Integer friendId);
}
