package ru.company.filmorate.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
        User user = createUser("name", "validLogin", "valid.email@example.com", 1990, 2, 12);

        mockMvcPerformPost(user)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("validLogin"))
                .andExpect(jsonPath("$.email").value("valid.email@example.com"))
                .andExpect(jsonPath("$.birthday").value("1990-02-12"));
    }

    @Test
    void shouldFailWhenLoginIsBlank() throws Exception {
        User user = createUser("name", "", "valid.email@example.com", 1990, 10, 10);

        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errors[0].field").value("login"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Логин не может быть пустым"));
    }

    @Test
    void shouldFailWhenLoginIsTooLong() throws Exception {
        User user = createUser("name", "loginloginloginloginloginloginloginloginloginlogin",
                "valid.email@example.com", 1990, 10, 10);

        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errors[0].field").value("login"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Логин должен быть короче 30 символов"));
    }

    @Test
    void shouldFailWhenUserNameIsTooLong() throws Exception {
        User user = createUser("namenamenamenamenamenamenamenamenamenamenamename",
                "login", "valid.email@example.com", 2000, 12, 12);

        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Имя должно быть короче 30 символов"));
    }

    @Test
    void shouldFailWhenEmailIsBlank() throws Exception {
        User user = createUser("name", "login", "", 1990, 12, 12);

        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Адрес электронной почты не может быть пустым"));
    }

    @Test
    void shouldFailWhenEmailIsNotValid() throws Exception {
        User user = createUser("name", "login", "not.valid.email", 1990, 12, 12);

        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Неверный формат электронной почты"));
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() throws Exception {
        User user = createUser("name", "login", "valid.email@example.com", 2990, 12, 12);

        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("birthday"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = createUser("name", "login", "valid.email@example.com", 2000, 12, 12);

        String response = mockMvcPerformPost(user)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);
        Integer userId = createdUser.getId();

        User updatedUser = createUser("name", "login", "valid.email@example.com", 1985, 12, 27);
        updatedUser.setId(userId);

        mockMvcPerformPut(updatedUser, updatedUser.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.email").value("valid.email@example.com"))
                .andExpect(jsonPath("$.birthday").value("1985-12-27"));
    }

    @Test
    void shouldFailWhenUserForUpdateIsNotFound() throws Exception {
        User user = createUser("name", "login", "valid.email@example.com", 2000, 12, 12);

        mockMvcPerformPut(user, 1000)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("пользователь не найден"));
    }

    @Test
    void shouldFailWhenUpdatedUserIdIsWrong() throws Exception {
        User user = createUser("name", "login", "valid.email@example.com", 2000, 12, 12);
        mockMvcPerformPost(user)
                .andExpect(status().isOk());

        User updatedUser = createUser("name", "login", "valid.email@example.com", 1985, 5, 15);
        updatedUser.setId(1000);

        mockMvcPerformPut(updatedUser, updatedUser.getId())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("пользователь не найден"));
    }

    @Test
    void shouldFailWhenUpdatedUserLoginIsBlank() throws Exception {
        User user = createUser("name", "login", "valid.email@example.com", 2000, 12, 12);
        user.setId(1);

        mockMvcPerformPost(user)
                .andExpect(status().isOk());

        User updatedUser = createUser("name", "", "valid.email@example.com", 2000, 12, 12);
        updatedUser.setId(1);

        mockMvcPerformPut(updatedUser, updatedUser.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("login"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Логин не может быть пустым"));
    }

    @Test
    void shouldFailWhenUpdatedUserNameIsTooLong() throws Exception {
        User user = createUser("name", "login", "valid.email@example.com", 2000, 12, 12);
        user.setId(1);

        mockMvcPerformPost(user)
                .andExpect(status().isOk());

        User updatedUser = createUser("namenamenamenamenamenamenamenamenamenamenamename",
                "login", "valid.email@example.com", 2000, 12, 12);
        updatedUser.setId(1);

        mockMvcPerformPut(updatedUser, updatedUser.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Имя должно быть короче 30 символов"));
    }

    @Test
    void shouldFailWhenUpdatedUserEmailIsBlank() throws Exception {
        User user = createUser("name", "login", "valid.email@example.com", 2000, 12, 12);
        user.setId(1);

        mockMvcPerformPost(user)
                .andExpect(status().isOk());

        User updatedUser = createUser("name", "login", "", 2000, 12, 12);
        updatedUser.setId(1);

        mockMvcPerformPut(updatedUser, updatedUser.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Адрес электронной почты не может быть пустым"));
    }

    @Test
    void shouldFailWhenUpdatedUserEmailIsNotValid() throws Exception {
        User user = createUser("name", "login", "valid.email@example.com", 2000, 12, 12);
        user.setId(1);

        mockMvcPerformPost(user)
                .andExpect(status().isOk());

        User updatedUser = createUser("name", "login", "not.valid.email.example.com", 2000, 12, 12);
        updatedUser.setId(1);

        mockMvcPerformPut(updatedUser, updatedUser.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Неверный формат электронной почты"));

    }

    @Test
    void shouldFailWhenUpdatedUserBirthdayIsInFuture() throws Exception {
        User user = createUser("name", "login", "valid.email@example.com", 2000, 12, 12);
        user.setId(1);

        mockMvcPerformPost(user)
                .andExpect(status().isOk());

        User updatedUser = createUser("name", "login", "valid.email@example.com", 3000, 12, 12);
        updatedUser.setId(1);

        mockMvcPerformPut(updatedUser, updatedUser.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("birthday"))
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        User user1 = createUser("name", "login1", "valid1.email@example.com", 2000, 1, 1);
        mockMvcPerformPost(user1)
                .andExpect(status().isOk());

        User user2 = createUser("name", "login2", "valid2.email@example.com", 2000, 1, 1);
        mockMvcPerformPost(user2)
                .andExpect(status().isOk());

        User user3 = createUser("name", "login3", "valid3.email@example.com", 2000, 1, 1);
        mockMvcPerformPost(user3)
                .andExpect(status().isOk());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].name").value("name"))
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
        User user = createUser("name", "login1", "valid1.email@example.com", 2000, 1, 1);

        mockMvcPerformPost(user)
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void shouldThrowDuplicateUserException() throws Exception {
        User user = createUser("name", "login1", "duplicate.email@example.com", 2000, 1, 1);

        mockMvcPerformPost(user)
                .andExpect(status().isOk());

        User duplicateUser = createUser("name", "login1", "duplicate.email@example.com", 2000, 1, 1);

        mockMvcPerformPost(duplicateUser)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("пользователь с такой электронной почтой уже существует"));

    }

    private User createUser(String name, String login, String email, int year, int month, int day) {
        return User.builder()
                .name(name)
                .login(login)
                .email(email)
                .birthday(LocalDate.of(year, month, day))
                .build();
    }

    private ResultActions mockMvcPerformPost(User user) throws Exception {
        return mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
    }

    private ResultActions mockMvcPerformPut(User updatedUser, int userId) throws Exception {
        return mockMvc.perform(put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)));
    }
}
