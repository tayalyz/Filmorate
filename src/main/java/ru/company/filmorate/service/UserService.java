package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.company.filmorate.exception.DuplicateUserException;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.User;
import ru.company.filmorate.storage.UserStorage;
import ru.company.filmorate.util.Identifier;

import java.util.List;

import static ru.company.filmorate.FilmorateApplication.log;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addUser(User user) {
        user.setId(Identifier.INSTANCE.generate(User.class));
        if (userStorage.isUserAlreadyExist(user)) {
            throw new DuplicateUserException("пользователь с такой электронной почтой уже существует");
        }
        userStorage.addUser(user);
        log.info("добавлен пользователь с id {}", user.getId());
        return user;
    }

    public User updateUser(User user, Integer id) {
        if (userStorage.compareIdsForUpdate(user, id)) {
            userStorage.updateUser(user, id);
            log.info("обновлен пользователь с id {}", user.getId());
            return user;
        } else {
            throw new NotFoundException("пользователь не найден");
        }
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
        Identifier.INSTANCE.clear(User.class);
        log.info("все пользователи удалены");
    }
}
