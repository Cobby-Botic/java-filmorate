package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    public Collection<User> getAllUsers();

    public User addUser(User newUser);

    public User getUserById(Long id);

    public User updateUser(User newUser);

    public void addFriend(Long userId, Long friendId);

    public List<User> getFriendsByUserId(Long id);

    public void deleteFriends(Long id, Long friendId);

    public List<User> getCommonFriends(Long userId, Long friendId);
}
