package ru.company.filmorate.storage;

import ru.company.filmorate.model.User;

import java.util.List;

public interface UserStorage<T> extends Storage<T>{

    User addFriend(Long userId, Long friendId);

    List<User> getFriends(Long id);

    void deleteFriend(Long id, Long friendId);
}
