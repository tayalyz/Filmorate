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

    public void addFriend(Long id, Long friendId) {
        if (!invalidFriendsIds(id, friendId)) {
            if (userContainsFriend(id, friendId)) {
                log.info("пользователь с id {} уже есть у вас в друзьях", friendId);
                throw new DuplicateUserException("такой пользователь уже есть у вас в друзьях");
            }

            userStorage.addFriend(id, friendId);
            log.info("пользователь с id {} добавил в друзья пользователя с id {}", id, friendId);
        }
    }

    public List<Long> getFriends(Long id) {
        if (userStorage.findById(id).isEmpty()) {
            log.info("пользователь с id {} не найден", id);
            throw new NotFoundException("пользователь не найден");
        }
        return userStorage.getFriends(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        if (!invalidFriendsIds(id, friendId) && userContainsFriend(id, friendId)) {
            userStorage.deleteFriend(id, friendId);
            log.info("пользователь с id {} удалил из друзей пользователя с id {}", id, friendId);
        }
    }

    public List<Long> getMutualFriends(Long id, Long otherId) {
        if (invalidFriendsIds(id, otherId)) {
            return new ArrayList<>();
        }
        List<Long> friends = userStorage.getFriends(id);
        friends.retainAll(userStorage.getFriends(otherId));
        return new ArrayList<>(friends);
    }

    private boolean invalidFriendsIds(Long id, Long friendId) {
        if (Objects.equals(id, friendId)) {
            throw new DuplicateUserException("нельзя добавить себя к себе в друзья");
        }
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException("пользователь не найден");
        }
        if (userStorage.findById(friendId).isEmpty()) {
            throw new NotFoundException("друг не найден");
        }
        return false;
    }

    private boolean userContainsFriend(Long id, Long friendId) {
        return userStorage.findById(id).isPresent() && userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"))
                .getFriends().contains(friendId);
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
