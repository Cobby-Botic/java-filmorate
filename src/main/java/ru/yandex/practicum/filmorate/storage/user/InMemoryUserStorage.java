package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> userMap = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return userMap.values();
    }

    @Override
    public User addUser(@Valid @RequestBody User newUser) {
        newUser.setId(getNextId());
        newUser.setFriends(new HashSet<>());
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        userMap.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User getUserById(Integer id) {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        }
        throw new NotFoundException("User not found.");
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        if (userMap.containsKey(id)) {
            return getAllUsers().stream()
                    .filter(user -> user.getFriends().contains(id))
                    .collect(Collectors.toList());
        }
        throw new NotFoundException("Пользователь не найден");
    }

    @Override
    public void deleteFriends(Integer id, Integer friendId) {
        getUserById(id).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(id);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        List<User> commonFriends = new ArrayList<>();
        for (Integer id :getUserById(userId).getFriends()) {
            if (getUserById(friendId).getFriends().contains(id)) {
                commonFriends.add(getUserById(id));
            }
        }
        return commonFriends;
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return user;
    }

    private Integer getNextId() {
        long currentMaxId = userMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return Math.toIntExact(++currentMaxId);
    }
}