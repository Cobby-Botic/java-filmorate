package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.Exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (userMap.containsKey(newUser.getId())) {
            User oldUser = userMap.get(newUser.getId());
            if (newUser.getName() != null && !newUser.getName().isBlank()) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getBirthday() != null && !newUser.getBirthday().isAfter(LocalDate.now())) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
                oldUser.setLogin(newUser.getLogin());
            }
            return oldUser;
        }
        throw new ConditionsNotMetException("Такого пользователя нет");
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
    public String deleteFriends(Integer id, Integer friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(id);
            return "Пользователь " + friend.getName() + "удален из списка друзей";
        }
        throw new NotFoundException("Пользователь с ID " + friendId + " в списке не найден");
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
