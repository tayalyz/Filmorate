package ru.company.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.company.filmorate.model.User;
import ru.company.filmorate.storage.userStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
public class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    public void testGetAllUsers() {
        List<User> users = userStorage.getAll();
        assertThat(users).isNotNull();
        assertThat(users).size().isEqualTo(3);
    }

    @Test
    public void testFindUserById_NotFound() {
        Optional<User> userOptional = userStorage.findById(99L);
        assertThat(userOptional).isNotPresent();
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.findById(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testAdd() {
        User user = User.builder().build();
        user.setEmail("u4@gmail.com");
        user.setLogin("login4");
        user.setName("name4");
        user.setBirthday(LocalDate.of(2008, 8, 28));

        User addedUser = userStorage.add(user);
        assertThat(addedUser).isNotNull();
        assertThat(userStorage.getAll()).size().isEqualTo(4);
    }

    @Test
    public void testUpdateUser() {
        User user = User.builder().build();
        user.setId(1L);
        user.setEmail("u4@gmail.com");
        user.setLogin("login4");
        user.setName("name4");
        user.setBirthday(LocalDate.of(2008, 8, 28));

        User updatedUser = userStorage.update(user);
        assertThat(updatedUser).isNotNull();
        assertThat(userStorage.getAll()).size().isEqualTo(3);
    }

    @Test
    public void testAddFriend() {
        userStorage.addFriend(2L,1L);
        Optional<User> user = userStorage.findById(2L);
        assertThat(user.get().getFriends()).containsExactly(1L);
    }

    @Test
    public void testDeleteFriend() {
        userStorage.addFriend(2L,1L);
        userStorage.deleteFriend(2L, 1L);
        assertThat(userStorage.findById(2L).get().getFriends()).isEmpty();
    }

    @Test
    public void testGetAllFriends() {
        userStorage.addFriend(2L,1L);
        userStorage.addFriend(2L,3L);

        List<Long> friendIds = userStorage.getFriends(2L).stream().map(User::getId).collect(Collectors.toList());
        assertThat(friendIds).size().isEqualTo(2);
        assertThat(friendIds).contains(1L).contains(3L);
    }
}
