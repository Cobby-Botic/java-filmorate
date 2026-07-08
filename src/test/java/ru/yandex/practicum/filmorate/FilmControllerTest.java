package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addNewFilm_shouldAddFilm_whenDataIsCorrect() throws Exception {

        String json = "{\n" +
                      "                  \"name\": \"Matrix\",\n" +
                      "                  \"description\": \"Good film\",\n" +
                      "                  \"releaseDate\": \"1999-03-31\",\n" +
                      "                  \"duration\": 136\n" +
                      "                }\n";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Matrix"));
    }

    @Test
    void addNewFilm_shouldReturn400_whenNameIsEmpty() throws Exception {

        String json = "{\n" +
                      "                  \"name\": \"\",\n" +
                      "                  \"description\": \"Good film\",\n" +
                      "                  \"releaseDate\": \"1999-03-31\",\n" +
                      "                  \"duration\": 136\n" +
                      "                }\n";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewFilm_shouldReturn400_whenDescriptionTooLong() throws Exception {

        String json = ("{\n" +
                       "                  \"name\": \"Matrix\",\n" +
                       "                  \"description\": \"%s\",\n" +
                       "                  \"releaseDate\": \"1999-03-31\",\n" +
                       "                  \"duration\": 136\n" +
                       "                }\n").formatted("a".repeat(201));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewFilm_shouldReturn400_whenDurationNegative() throws Exception {

        String json = "{\n" +
                      "                  \"name\": \"Matrix\",\n" +
                      "                  \"description\": \"Good film\",\n" +
                      "                  \"releaseDate\": \"1999-03-31\",\n" +
                      "                  \"duration\": -1\n" +
                      "                }\n";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFilms_shouldReturnFilms() throws Exception {
        String json = "{\n" +
                      "                  \"name\": \"Matrix\",\n" +
                      "                  \"description\": \"Good film\",\n" +
                      "                  \"releaseDate\": \"1999-03-31\",\n" +
                      "                  \"duration\": 136\n" +
                      "                }\n";

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void updateFilm_shouldUpdateFilm_whenFilmExists() throws Exception {

        String createJson = "{\n" +
                            "                  \"name\": \"Matrix\",\n" +
                            "                  \"description\": \"Good film\",\n" +
                            "                  \"releaseDate\": \"1999-03-31\",\n" +
                            "                  \"duration\": 136\n" +
                            "                }\n";

        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String updateJson = "{\n" +
                            "                  \"id\": 1,\n" +
                            "                  \"name\": \"Matrix Reloaded\"\n" +
                            "                }\n";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Matrix Reloaded"));
    }
}