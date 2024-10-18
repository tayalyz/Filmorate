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
        User user = createUser("validLogin", "valid.email@example.com", 1990, 2, 12);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("validLogin"))
                .andExpect(jsonPath("$.email").value("valid.email@example.com"))
                .andExpect(jsonPath("$.birthday").value("1990-02-12"));
    }

    @Test
    void shouldFailWhenLoginIsBlank() throws Exception {
        User user = createUser("", "valid.email@example.com", 1990, 10, 10);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("login"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Логин не может быть пустым"));
    }

    @Test
    void shouldFailWhenEmailIsBlank() throws Exception {
        User user = createUser("login", "", 1990, 12, 12);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Адрес электронной почты не может быть пустым"));
    }

    @Test
    void shouldFailWhenEmailIsNotValid() throws Exception {
        User user = createUser("login", "not.valid.email", 1990, 12, 12);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Неверный формат электронной почты"));
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() throws Exception {
        User user = createUser("login", "valid.email@example.com", 2990, 12, 12);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("birthday"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = createUser("login", "valid.email@example.com", 2000, 12, 12);

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);
        Integer userId = createdUser.getId();

        User updatedUser = createUser("login", "valid.email@example.com", 1985, 12, 27);
        updatedUser.setId(userId);

        mockMvc.perform(put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.email").value("valid.email@example.com"))
                .andExpect(jsonPath("$.birthday").value("1985-12-27"));
    }

    @Test
    void shouldFailWhenUserForUpdateIsNotFound() throws Exception {
        User user = createUser("login", "valid.email@example.com", 2000, 12, 12);

        mockMvc.perform(put("/users/100000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenUpdatedUserIdIsWrong() throws Exception {
        User user = createUser("login", "valid.email@example.com", 2000, 12, 12);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = createUser("login", "valid.email@example.com", 1985, 5, 15);
        updatedUser.setId(1000);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenUpdatedUserLoginIsBlank() throws Exception {
        User user = createUser("login", "valid.email@example.com", 2000, 12, 12);
        user.setId(1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = createUser("", "valid.email@example.com", 2000, 12, 12);
        updatedUser.setId(1);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("login"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Логин не может быть пустым"));

    }

    @Test
    void shouldFailWhenUpdatedUserEmailIsBlank() throws Exception {
        User user = createUser("login", "valid.email@example.com", 2000, 12, 12);
        user.setId(1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = createUser("login", "", 2000, 12, 12);
        updatedUser.setId(1);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Адрес электронной почты не может быть пустым"));
    }

    @Test
    void shouldFailWhenUpdatedUserEmailIsNotValid() throws Exception {
        User user = createUser("login", "valid.email@example.com", 2000, 12, 12);
        user.setId(1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = createUser("login", "not.valid.email.example.com", 2000, 12, 12);
        updatedUser.setId(1);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Неверный формат электронной почты"));

    }

    @Test
    void shouldFailWhenUpdatedUserBirthdayIsInFuture() throws Exception {
        User user = createUser("login", "valid.email@example.com", 2000, 12, 12);
        user.setId(1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        User updatedUser = createUser("login", "valid.email@example.com", 3000, 12, 12);
        updatedUser.setId(1);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("birthday"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        User user1 = createUser("login1", "valid1.email@example.com", 2000, 1, 1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        User user2 = createUser("login2", "valid2.email@example.com", 2000, 1, 1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk());

        User user3 = createUser("login3", "valid3.email@example.com", 2000, 1, 1);

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
        User user = createUser("login1", "valid1.email@example.com", 2000, 1, 1);

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

    private User createUser(String login1, String mail, int year, int month, int day) {
        User user = new User();
        user.setLogin(login1);
        user.setEmail(mail);
        user.setBirthday(LocalDate.of(year, month, day));
        return user;
    }
}
