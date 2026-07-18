package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class FilmoRateApplicationTests {

    @Autowired
    private UserDbStorage userStorage;


    @Test
    public void testFindUserById() {
        User user = new User();

        user.setLogin("testLogin");
        user.setEmail("test@test.com");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = userStorage.addUser(user);

        User result = userStorage.getUserById(savedUser.getId());

        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", savedUser.getId());
    }
}