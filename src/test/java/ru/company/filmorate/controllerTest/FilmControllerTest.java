package ru.company.filmorate.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
        Film film = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);

        mockMvcPerformPost(film)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.releaseDate").value("2024-10-17"))
                .andExpect(jsonPath("$.duration").value("200"));
    }

    @Test
    void shouldFailWhenNameIsBlank() throws Exception {
        Film film = createFilm("", "desc", LocalDate.of(2024, 10, 17), 200);

        mockMvcPerformPost(film)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Название фильма не может быть пустым"));
    }

    @Test
    void shouldFailWhenNameIsTooLong() throws Exception {
        Film film = createFilm("namenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamename",
                "desc", LocalDate.of(2024, 10, 17), 200);

        mockMvcPerformPost(film)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Название фильма должно быть короче 50 символов"));
    }

    @Test
    void shouldFailWhenDurationIsNegative() throws Exception {
        Film film = createFilm("name", "desc", LocalDate.of(2024, 10, 17), -200);

        mockMvcPerformPost(film)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("duration"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Продолжительность фильма должна быть положительной"));
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() throws Exception {
        Film film = createFilm("name", "descdescdescdescvvdescdescdescdescdescdescdescdescdescdescdesc" +
                        "descdescdescdescdescdescdescdescdescdescdescdescdescvdescdescdescdesc" +
                        "descdescdescdescdescdescdescdescdescdescdescdescvvdescdescdescdescdesc" +
                        "descdescdescdescdescdescdescdescdescdescdescdescdescdescdescdescdescdesc" +
                        "descvdescdescdescdescdescdescdescdescdescdescdescdesc",
                LocalDate.of(2024, 10, 17), 200);

        mockMvcPerformPost(film)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("description"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Описание фильма слишком длинное"));
    }

    @Test
    void shouldFailWhenReleaseDateIsNotValid() throws Exception {
        Film film = createFilm("name", "desc", LocalDate.of(1000, 10, 17), 200);

        mockMvcPerformPost(film)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("releaseDate"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldUpdateFilm() throws Exception {
        Film film = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);

        String response = mockMvcPerformPost(film)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(response, Film.class);
        Integer filmId = createdFilm.getId();

        Film updatedFilm = createFilm("updName", "desc", LocalDate.of(1895, 12, 28), 200);
        updatedFilm.setId(filmId);

        mockMvcPerformPut(updatedFilm, filmId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updName"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.releaseDate").value("1895-12-28"))
                .andExpect(jsonPath("$.duration").value("200"));
    }

    @Test
    void shouldFailWhenUpdateFilmIsNotFound() throws Exception {
        Film film = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);
        film.setId(1);

        mockMvcPerformPut(film, film.getId())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("фильм не найден"));
    }

    @Test
    void shouldFailWhenUpdatedFilmIdIsWrong() throws Exception {
        Film film = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);

        mockMvcPerformPost(film)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Film updatedFilm = createFilm("name", "desc", LocalDate.of(2023, 10, 17), 200);
        updatedFilm.setId(1000);

        mockMvcPerformPut(updatedFilm, 1)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("фильм не найден"));
    }

    @Test
    void shouldFailWhenUpdatedFilmDurationIsNegative() throws Exception {
        Film film = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);

        String response = mockMvcPerformPost(film)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(response, Film.class);
        Integer filmId = createdFilm.getId();

        Film updatedFilm = createFilm("name", "desc", LocalDate.of(2024, 10, 17), -200);
        updatedFilm.setId(filmId);

        mockMvcPerformPut(updatedFilm, filmId)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("duration"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Продолжительность фильма должна быть положительной"));

    }

    @Test
    void shouldFailWhenUpdatedFilmReleaseDateIsNotValid() throws Exception {
        Film film = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);

        String response = mockMvcPerformPost(film)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(response, Film.class);
        Integer filmId = createdFilm.getId();

        Film updatedFilm = createFilm("name", "desc", LocalDate.of(1895, 12, 27), 200);
        updatedFilm.setId(filmId);

        mockMvcPerformPut(updatedFilm, filmId)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("releaseDate"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Дата релиза не может быть раньше 28 декабря 1895 года"));

    }

    @Test
    void shouldGetAllFilms() throws Exception {
        Film film1 = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);

        mockMvcPerformPost(film1)
                .andExpect(status().isOk());

        Film film2 = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);

        mockMvcPerformPost(film2)
                .andExpect(status().isOk());

        Film film3 = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);

        mockMvcPerformPost(film3)
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
    void shouldDeleteAllFilms() throws Exception {
        Film film = createFilm("name", "desc", LocalDate.of(2024, 10, 17), 200);

        mockMvcPerformPost(film)
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    private Film createFilm(String name, String description, LocalDate releaseDate, int duration) {
        return Film.builder()
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();
    }

    private ResultActions mockMvcPerformPost(Film film) throws Exception {
        return mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));
    }

    private ResultActions mockMvcPerformPut(Film updatedFilm, int filmId) throws Exception {
        return mockMvc.perform(put("/films/" + filmId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFilm)));
    }
}

