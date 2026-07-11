package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    public Collection<User> getAllUsers();

    public User addUser(@Valid @RequestBody User newUser);

    public User updateUser(@RequestBody User newUser);

    public User getUserById(Integer id);

    public User addFriend(Integer userId, Integer friendId);

    public List<User> getFriendsByUserId(Integer id);

    public void deleteFriends(Integer id, Integer friendId);

    public List<User> getCommonFriends(Integer userId, Integer friendId);
}
