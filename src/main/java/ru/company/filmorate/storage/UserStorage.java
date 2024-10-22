package ru.company.filmorate.storage;

import ru.company.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    boolean userExists(User user);

    List<User> getAllUsers();

    void deleteAllUsers();

    User updateUser(User user, Integer id);

    boolean compareIdsForUpdate(Integer existingUserId, Integer pathId);
}
