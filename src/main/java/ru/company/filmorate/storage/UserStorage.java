package ru.company.filmorate.storage;

import ru.company.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> addUser(User user);

    List<User> getAllUsers();

    void deleteAllUsers();

    Optional<User> updateUser(User user, Long id);

    void addFriend(Long userId, Long friendId);

    List<Long> getFriends(Long id);

    void deleteFriend(Long id, Long friendId);

    List<Long> getMutualFriends(Long id, Long otherId);

    boolean userExistsById(Long id);
}
