package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.company.filmorate.exception.DuplicateUserException;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.User;
import ru.company.filmorate.storage.userStorage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User getUserById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> {
                    log.info("пользователь с id " + id + " не найден");
                    return new NotFoundException("пользователь с id " + id + " не найден");
                });
    }

    public User addUser(User user) {
        if (!userExistsByEmailAndLogin(user)) {
            validateUser(user);
            userStorage.add(user);
            log.info("добавлен пользователь с id {}", user.getId());
        }
        return user;
    }

    public User updateUser(User user) {
        getUserById(user.getId());
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

    // TODO проверка возвращает пользователя, добавление - булен, метод вренет пользовтаеля
    public User addFriend(Long id, Long friendId) {
        validateFriendsIds(id, friendId);
        if (userContainsFriend(id, friendId)) {
            throw new DuplicateUserException("такой пользователь уже есть у вас в друзьях");
        }
        User user = userStorage.addFriend(id, friendId);
        log.info("пользователь с id {} добавил в друзья пользователя с id {}", id, friendId);
        return user;
    }

    public List<User> getFriends(Long id) {
        getUserById(id);
        return userStorage.getFriends(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        validateFriendsIds(id, friendId);

        userStorage.deleteFriend(id, friendId);
        log.info("пользователь с id {} удалил из друзей пользователя с id {}", id, friendId);
    }

    public List<User> getMutualFriends(Long id, Long otherId) {
        validateFriendsIds(id, otherId);

        List<User> friends = userStorage.getFriends(id);
        List<User> otherFriends = userStorage.getFriends(otherId);

        friends.retainAll(otherFriends);
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
                .getFriends().contains(friendId);
    }

    private boolean userExistsByEmailAndLogin(User user) {
        Set<String> emails = new HashSet<>();
        Set<String> logins = new HashSet<>();

        for (User existingUser : userStorage.getAll()) {
            emails.add(existingUser.getEmail());
            logins.add(existingUser.getLogin());
        }

        boolean emailExists = emails.contains(user.getEmail());
        boolean loginExists = logins.contains(user.getLogin());

        if (emailExists || loginExists) {
            String message = buildDuplicateUserMessage(emailExists, loginExists);
            log.info("пользователь с электронной почтой {} или логином {} уже существует", user.getEmail(), user.getLogin());
            throw new DuplicateUserException(message);
        }
        return false;
    }

    private String buildDuplicateUserMessage(boolean emailExists, boolean loginExists) {
        if (emailExists && loginExists) {
            return "пользователь с такими логином и электронной почтой уже существует";
        } else if (emailExists) {
            return "пользователь с такой электронной почтой уже существует";
        } else {
            return "пользователь с таким логином уже существует";
        }
    }

    private void validateUser(User user) {
        Set<Long> friends = user.getFriends();
        if (friends != null && !friends.isEmpty()) {
            friends.forEach(userStorage::findById);
        }
    }
}
