package ru.company.filmorate.storageTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.company.filmorate.model.User;
import ru.company.filmorate.storage.InMemoryUserStorage;
import ru.company.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(UserStorage.class)
public class UserStorageTest {

    @Autowired
    private InMemoryUserStorage userStorage;

    @AfterEach
    public void clear() {
        userStorage.deleteAllUsers();
    }

    @Test
    public void addUser() {
        User user = createUser("user", "user", "a@mail.ru", LocalDate.of(2000, 10 ,1));

        Optional<User> createdUser = userStorage.addUser(user);
        assertNotNull(createdUser);
        assertEquals(user, createdUser.get());
    }

    @Test
    public void updateUser() {
        User user = createUser("user", "user", "a@mail.ru", LocalDate.of(2000, 10 ,1));
        userStorage.addUser(user);

        User userForUpdate = createUser("user", "user", "b@mail.ru", LocalDate.of(2000, 10 ,1));
        userForUpdate.setId(1L);
        Optional<User> createdUser = userStorage.updateUser(userForUpdate, 1L);

        assertNotNull(createdUser);
        assertEquals(userForUpdate, createdUser.get());
    }

    @Test
    public void getAllUsers() {
        User user = createUser("user", "user", "a@mail.ru", LocalDate.of(2000, 10 ,1));
        userStorage.addUser(user);

        User userForUpdate = createUser("user", "user", "b@mail.ru", LocalDate.of(2000, 10 ,1));
        userStorage.addUser(userForUpdate);

        List<User> expectedUsers = List.of(user, userForUpdate);
        List<User> users = userStorage.getAllUsers();

        assertNotNull(users);
        assertEquals(expectedUsers, users);
        assertEquals(2, users.size());
    }

    @Test
    public void deleteAllUsers() {
        User user = createUser( "user", "user", "a@mail.ru", LocalDate.of(2000, 10 ,1));
        userStorage.addUser(user);

        User userForUpdate = createUser("user", "user", "b@mail.ru", LocalDate.of(2000, 10 ,1));
        userStorage.addUser(userForUpdate);

        userStorage.deleteAllUsers();

        List<User> expectedUsers = userStorage.getAllUsers();
        assertEquals(0, expectedUsers.size());
    }

    private User createUser(String name, String login, String email, LocalDate date) {
        User user = new User();
        user.setLogin(login);
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(date);
        user.setFriends(new HashSet<>());
        return user;
    }
}