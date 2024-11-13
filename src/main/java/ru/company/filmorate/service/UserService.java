package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.company.filmorate.exception.DuplicateUserException;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.User;
import ru.company.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage<User> userStorage;

    private User getUserById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь с id " + id + " не найден"));
    }

    public User addUser(User user) {
        if (!userExistsByEmailAndLogin(user)) {
            userStorage.add(user);
            log.info("добавлен пользователь с id {}", user.getId());
        }
        return user;
    }

    public User updateUser(User user) {
        if (userStorage.findById(user.getId()).isEmpty()) {
            log.info("пользователь с id {} не найден", user.getId());
            throw new NotFoundException("пользователь не найден");
        }
        userStorage.update(user);
        log.info("обновлен пользователь с id {}", user.getId());
        return user;
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public void deleteAllUsers() {
        userStorage.deleteAll();
        log.info("все пользователи удалены");
    }

    public User addFriend(Long id, Long friendId) {

        validateFriendsIds(id, friendId);

        User user = userStorage.addFriend(id, friendId);
        log.info("пользователь с id {} добавил в друзья пользователя с id {}", id, friendId);
        return user;

    }

    public List<User> getFriends(Long id) {
        return userStorage.getFriends(getUserById(id).getId());
    }

    public void deleteFriend(Long id, Long friendId) {
        validateFriendsIds(id, friendId);
        if (userContainsFriend(id, friendId)) {
            userStorage.deleteFriend(id, friendId);
            log.info("пользователь с id {} удалил из друзей пользователя с id {}", id, friendId);
        }
    }

    public List<User> getMutualFriends(Long id, Long otherId) {
        validateFriendsIds(id, otherId);

        List<User> friends = userStorage.getFriends(id);
        friends.retainAll(userStorage.getFriends(otherId));
        return new ArrayList<>(friends);
    }

    private void validateFriendsIds(Long id, Long friendId) {
        if (Objects.equals(id, friendId)) {
            throw new DuplicateUserException("нельзя добавить себя к себе в друзья");
        }
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException("пользователь не найден");
        }
        if (userStorage.findById(friendId).isEmpty()) {
            throw new NotFoundException("друг не найден");
        }
    }

    private boolean userContainsFriend(Long id, Long friendId) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"))
                .getFriends().contains(friendId) && userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"))
                .getFriends().contains(id);
    }

    private boolean userExistsByEmailAndLogin(User user) {
        for (User existingUser : userStorage.getAll()) {
            if (existingUser.getEmail().equals(user.getEmail()) && existingUser.getLogin().equals(user.getLogin())) {
                log.info("пользователь с электронной почтой {} и логином {} уже существует", user.getEmail(), user.getLogin());
                throw new DuplicateUserException("такой пользователь уже существует");
            }
            if (existingUser.getEmail().equals(user.getEmail())) {
                log.info("пользователь с электронной почтой {} уже существует", user.getEmail());
                throw new DuplicateUserException("пользователь с такой электронной почтой уже существует");
            }
            if (existingUser.getLogin().equals(user.getLogin())) {
                log.info("пользователь с логином {} уже существует", user.getLogin());
                throw new DuplicateUserException("пользователь с таким логином уже существует");
            }
        }
        return false;
    }
}
