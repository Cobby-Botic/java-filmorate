package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;
import ru.yandex.practicum.filmorate.Exception.ConditionsNotMetException;

import java.time.LocalDate;
import java.util.Collection;

@SpringBootTest
class UserContollerTest {
    UserController userController = new UserController();

	@Test
    void addUser_shouldAddUser_whenDataIsCorrect() {
        User user = new User();
        user.setLogin("Vasya");
        user.setName("Василий");
        user.setBirthday(LocalDate.of(2001, 2, 12));
        user.setEmail("Vasiliy19@gmail.com");

        User savedUser = userController.addUser(user);

        assertNotNull(savedUser.getId());
        assertEquals("Vasiliy19@gmail.com", savedUser.getEmail());
    }
    @Test
    void addUser_shouldThrowException_whenEmailIsEmpty() {
        UserController controller = new UserController();

        User user = new User();
        user.setEmail("");

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> controller.addUser(user)
        );

        assertEquals("электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void getUsers_shouldReturnAllUsers() {
        UserController controller = new UserController();

        User firstUser = new User();
        firstUser.setLogin("first");
        firstUser.setBirthday(LocalDate.of(2001, 2, 12));
        firstUser.setEmail("first@mail.ru");

        User secondUser = new User();
        secondUser.setBirthday(LocalDate.of(2001, 2, 12));
        secondUser.setLogin("second");
        secondUser.setEmail("second@mail.ru");

        controller.addUser(firstUser);
        controller.addUser(secondUser);

        Collection<User> users = controller.getAllUsers();

        assertEquals(2, users.size());
    }

}
