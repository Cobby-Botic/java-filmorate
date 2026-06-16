package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> userMap = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрос всех юзеров");
        return userMap.values();
    }

    @PostMapping
    public User addUser(@Valid  @RequestBody User newUser) {
        newUser.setId(getNextId());
        if (newUser.getName().isBlank() || newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
        }
        userMap.put(newUser.getId(), newUser);
        log.info("Добавление нового пользователя");
        return newUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.info("Запрос на обновление данных пользователя");
        if (newUser.getId() == null) {
            log.warn("В запросе не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (userMap.containsKey(newUser.getId())) {
            log.info("Пользователь с id найден");
            User oldUser = userMap.get(newUser.getId());
            if (newUser.getName() != null && !newUser.getName().isBlank()) {
                log.info("Обновление имени пользователя");
                oldUser.setName(newUser.getName());
            }
            if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
                log.info("Обновление эмейла");
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getBirthday() != null && !newUser.getBirthday().isAfter(LocalDate.now())) {
                log.info("Обновление даты рождения");
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
                log.info("Обновление логина");
                oldUser.setLogin(newUser.getLogin());
            }
            return oldUser;
        }
        log.warn("User с id не найден");
        throw new ConditionsNotMetException("Такого пользователя нет");
    }

    private Integer getNextId() {
        log.info("Генерация нового id");
        long currentMaxId = userMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return Math.toIntExact(++currentMaxId);
    }

    public void clear() {
        userMap.clear();
    }
}
