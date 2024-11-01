package ru.company.filmorate.storage;

import java.util.List;

public interface UserStorage<T> extends Storage<T>{

    void addFriend(Long userId, Long friendId);

    List<Long> getFriends(Long id);

    void deleteFriend(Long id, Long friendId);
}
