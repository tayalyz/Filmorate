package ru.company.filmorate.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.company.filmorate.controller.FilmController;
import ru.company.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clear() throws Exception {
        mockMvc.perform(delete("/films"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddFilm() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(200);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("film"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.releaseDate").value("2024-10-17"))
                .andExpect(jsonPath("$.duration").value("200"));
    }

    @Test
    void shouldFailWhenNameIsBlank() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(200);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Название фильма не может быть пустым"));
    }

    @Test
    void shouldFailWhenDurationIsNegative() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(-200);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("duration"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Продолжительность фильма должна быть положительной"));
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("descdescdescdescvvdescdescdescdescdescdescdescdescdescdescdesc" +
                "descdescdescdescdescdescdescdescdescdescdescdescdescvdescdescdescdesc" +
                "descdescdescdescdescdescdescdescdescdescdescdescvvdescdescdescdescdesc" +
                "descdescdescdescdescdescdescdescdescdescdescdescdescdescdescdescdescdesc" +
                "descvdescdescdescdescdescdescdescdescdescdescdescdesc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(200);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("description"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Описание фильма слишком длинное"));
    }

    @Test
    void shouldFailWhenReleaseDateIsNotValid() throws Exception { // todo не работает аннотация
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(200);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("releaseDate"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldUpdateFilm() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(200);

        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(response, Film.class);
        Integer filmId = createdFilm.getId();

        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("updName");
        updatedFilm.setDescription("desc");
        updatedFilm.setReleaseDate(LocalDate.of(2024, 10, 17));
        updatedFilm.setDuration(200);

        mockMvc.perform(put("/films/" + filmId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updName"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.releaseDate").value("2024-10-17"))
                .andExpect(jsonPath("$.duration").value("200"));
    }

    @Test
    void shouldFailWhenUpdateFilmIsNotFound() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(200);

        mockMvc.perform(put("/films/100000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenUpdatedFilmIdIsWrong() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(200);

        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Film updatedFilm = new Film();
        updatedFilm.setId(1000);
        updatedFilm.setName("updName");
        updatedFilm.setDescription("desc");
        updatedFilm.setReleaseDate(LocalDate.of(2024, 10, 17));
        updatedFilm.setDuration(200);

        mockMvc.perform(put("/films/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenUpdatedFilmDurationIsNegative() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(200);

        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(response, Film.class);
        Integer filmId = createdFilm.getId();

        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("updName");
        updatedFilm.setDescription("desc");
        updatedFilm.setReleaseDate(LocalDate.of(2024, 10, 17));
        updatedFilm.setDuration(-200000);

        mockMvc.perform(put("/films/" + updatedFilm.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("duration"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Продолжительность фильма должна быть положительной"));

    }

    @Test
    void shouldFailWhenUpdatedFilmReleaseDateIsNotValid() throws Exception { // todo
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(200);

        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(response, Film.class);
        Integer filmId = createdFilm.getId();

        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("updName");
        updatedFilm.setDescription("desc");
        updatedFilm.setReleaseDate(LocalDate.of(1000, 10, 17));
        updatedFilm.setDuration(200);

        mockMvc.perform(put("/films/" + updatedFilm.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("releaseDate"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Неверный формат электронной почты"));

    }

    @Test
    void shouldGetAllFilms() throws Exception {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("desc");
        film1.setReleaseDate(LocalDate.of(2024, 10, 17));
        film1.setDuration(200);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        Film film2 = new Film();
        film2.setName("name");
        film2.setDescription("desc");
        film2.setReleaseDate(LocalDate.of(2024, 10, 17));
        film2.setDuration(200);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().isOk());

        Film film3 = new Film();
        film3.setName("name");
        film3.setDescription("desc");
        film3.setReleaseDate(LocalDate.of(2024, 10, 17));
        film3.setDuration(200);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film3)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("desc"))
                .andExpect(jsonPath("$[0].releaseDate").value("2024-10-17"))
                .andExpect(jsonPath("$[0].duration").value("200"));
    }

    @Test
    void shouldGetAllFilmsWhenUsersAreEmpty() throws Exception {
        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void shouldDeleteAllUsers() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2024, 10, 17));
        film.setDuration(200);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}

