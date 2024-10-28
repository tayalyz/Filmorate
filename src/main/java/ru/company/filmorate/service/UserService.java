package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.company.filmorate.model.User;
import ru.company.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Optional<User> addUser(User user) {
        userStorage.addUser(user);
        log.info("добавлен пользователь с id {}", user.getId());
        return Optional.of(user);
    }

    public  Optional<User> updateUser(User user, Long id) {
        userStorage.updateUser(user, id);
        log.info("обновлен пользователь с id {}", id);
        return Optional.of(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
        log.info("все пользователи удалены");
    }

    public void addFriend(Long id, Long friendId) {
        userStorage.addFriend(id, friendId);
        log.info("пользователь с id {} добавил в друзья пользователя с id {}", id, friendId);
    }

    public List<Long> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        userStorage.deleteFriend(id, friendId);
        log.info("пользователь с id {} удалил из друзей пользователя с id {}", id, friendId);
    }

    public List<Long> getMutualFriends(Long id, Long otherId) {
        return userStorage.getMutualFriends(id, otherId);
    }
}
