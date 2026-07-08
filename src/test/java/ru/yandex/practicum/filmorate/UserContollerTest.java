package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserContollerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @BeforeEach
    void clear() {
        userController.clear();
    }

    @Test
    void addUser_shouldAddUser_whenDataIsCorrect() throws Exception {
        String json = "{\n" +
                      "                \"login\": \"vasyan\",\n" +
                      "                \"name\": \"Василий\",\n" +
                      "                \"email\": \"Vasyan69@mail.ru\",\n" +
                      "                \"birthday\": \"2001-02-12\"\n" +
                      "                }\n";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("Vasyan69@mail.ru"));
    }

    @Test
    void addUser_shouldThrowException_whenEmailIsEmpty() throws Exception {
        String json = "{\n" +
                      "                \"login\": \"vasyan\",\n" +
                      "                \"name\": \"Василий\",\n" +
                      "                \"email\": \"\",\n" +
                      "                \"birthday\": \"2001-02-12\"\n" +
                      "                }\n";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsers_shouldReturnAllUsers() throws Exception {
        String json = "{\n" +
                      "                \"login\": \"vasyan\",\n" +
                      "                \"name\": \"Василий\",\n" +
                      "                \"email\": \"Vasyan69@mail.ru\",\n" +
                      "                \"birthday\": \"2001-02-12\"\n" +
                      "                }\n";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}