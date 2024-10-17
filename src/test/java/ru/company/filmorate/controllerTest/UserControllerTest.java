package ru.company.filmorate.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.company.filmorate.controller.UserController;
import ru.company.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clear() throws Exception {
        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddUser() throws Exception {
        User user = new User();
        user.setLogin("validLogin");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("validLogin"))
                .andExpect(jsonPath("$.email").value("valid.email@example.com"))
                .andExpect(jsonPath("$.birthday").value("1990-01-01"));
    }

    @Test
    void shouldFailWhenLoginIsBlank() throws Exception {
        User user = new User();
        user.setLogin("");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("login"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Логин не может быть пустым"));
    }

    @Test
    void shouldFailWhenEmailIsBlank() throws Exception {
        User user = new User();
        user.setLogin("login");
        user.setEmail("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Адрес электронной почты не может быть пустым"));
    }

    @Test
    void shouldFailWhenEmailIsNotValid() throws Exception {
        User user = new User();
        user.setLogin("login");
        user.setEmail("not.valid.email");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Неверный формат электронной почты"));
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() throws Exception {
        User user = new User();
        user.setLogin("login");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(2990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("birthday"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = new User();
        user.setLogin("login");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);
        Integer userId = createdUser.getId();

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setLogin("updatedLogin");
        updatedUser.setEmail("updated.email@example.com");
        updatedUser.setBirthday(LocalDate.of(1985, 5, 15));

        mockMvc.perform(put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("updatedLogin"))
                .andExpect(jsonPath("$.email").value("updated.email@example.com"))
                .andExpect(jsonPath("$.birthday").value("1985-05-15"));
    }

    @Test
    void shouldFailWhenUpdatedUserNotFound() throws Exception {
        User user = new User();
        user.setId(100000);
        user.setLogin("validLogin");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(put("/users/100000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenUpdatedUserIdIsWrong() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("login");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = new User();
        updatedUser.setId(1000);
        updatedUser.setLogin("updatedLogin");
        updatedUser.setEmail("updated.email@example.com");
        updatedUser.setBirthday(LocalDate.of(1985, 5, 15));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenUpdatedUserLoginIsBlank() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("login");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setLogin("");
        updatedUser.setEmail("updated.email@example.com");
        updatedUser.setBirthday(LocalDate.of(1985, 5, 15));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("login"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Логин не может быть пустым"));

    }

    @Test
    void shouldFailWhenUpdatedUserEmailIsBlank() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("login");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setLogin("login");
        updatedUser.setEmail("");
        updatedUser.setBirthday(LocalDate.of(1985, 5, 15));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Адрес электронной почты не может быть пустым"));

    }

    @Test
    void shouldFailWhenUpdatedUserEmailIsNotValid() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("login");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setLogin("login");
        updatedUser.setEmail("bad.updated.email");
        updatedUser.setBirthday(LocalDate.of(1985, 5, 15));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Неверный формат электронной почты"));

    }

    @Test
    void shouldFailWhenUpdatedUserBirthdayIsInFuture() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("login");
        user.setEmail("valid.email@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setLogin("login");
        updatedUser.setEmail("valid.email@example.com");
        updatedUser.setBirthday(LocalDate.of(3000, 5, 15));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("birthday"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setLogin("login1");
        user1.setEmail("valid1.email@example.com");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setLogin("login2");
        user2.setEmail("valid2.email@example.com");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk());

        User user3 = new User();
        user3.setLogin("login3");
        user3.setEmail("valid3.email@example.com");
        user3.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user3)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].login").value("login1"))
                .andExpect(jsonPath("$[0].email").value("valid1.email@example.com"))
                .andExpect(jsonPath("$[0].birthday").value("2000-01-01"));
    }

    @Test
    void shouldGetAllUsersWhenUsersAreEmpty() throws Exception {
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void shouldDeleteAllUsers() throws Exception {
        User user = new User();
        user.setLogin("login1");
        user.setEmail("valid1.email@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}
