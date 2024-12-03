package ru.company.filmorate.storage.userStorage;

import ru.company.filmorate.model.User;
import ru.company.filmorate.storage.Storage;

import java.util.List;

public interface UserStorage extends Storage<User> {

    User addFriend(Long userId, Long friendId);

    List<User> getFriends(Long id);

    void deleteFriend(Long id, Long friendId);
}
