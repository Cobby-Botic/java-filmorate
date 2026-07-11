package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final UserStorage storage;

    @GetMapping
    public Collection<User> getUsers() {
        return storage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return storage.getUserById(id);
    }

    @PostMapping
    public User addNewUser(@Valid @RequestBody User newUser) {
        log.info("Запрос на добавление пользователя {}", newUser.getName());
        return storage.addUser(newUser);
    }

    @PutMapping
    public User updateUserInfo(@Valid @RequestBody User newUser) {
        log.info("Запрос на изменение данных пользователя");
        return storage.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriendUser(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Запрос на добавление в друзья");
        return service.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsUser(@PathVariable Integer id) {
        return service.getFriendsByUserId(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendUser(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен запрос на удаление пользователя из списка друзей");
        service.deleteFriendByUser(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Запрос на получение общих друзей");
        return service.getCommonFriends(id, friendId);
    }
}