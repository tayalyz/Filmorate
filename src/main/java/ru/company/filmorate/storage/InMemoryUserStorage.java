package ru.company.filmorate.storage;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.company.filmorate.exception.DuplicateUserException;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.User;
import ru.company.filmorate.util.Identifier;

import java.util.*;

@Component
@RequiredArgsConstructor
@Data
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> addUser(User user) {
        if (!userExistsByEmail(user)) {
            user.setId(Identifier.INSTANCE.generate(User.class));
            users.put(user.getId(), user);
            return Optional.of(user);
        }
        throw new DuplicateUserException("пользователь с такой электронной почтой уже существует");
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
        Identifier.INSTANCE.clear(User.class);
    }

    @Override
    public Optional<User> updateUser(User user, Long id) {
        if (compareIdsForUpdate(user.getId(), id)) {
            users.remove(id);
            users.put(id, user);
            return Optional.of(user);
        }
        throw new NotFoundException("пользователь не найден");
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        if (checkFriendsIds(id, friendId) && !userContainsFriend(id, friendId)) {
            users.get(id).getFriends().add(friendId);
        } else {
            throw new NotFoundException("пользователь не найден");
        }
    }

    @Override
    public List<Long> getFriends(Long id) {
        if (userExistsById(id)) {
            return new ArrayList<>(users.get(id).getFriends());
        }
        throw new NotFoundException("пользователь не найден");
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        if (checkFriendsIds(id, friendId) && userContainsFriend(id, friendId)) {
            users.get(id).getFriends().remove(friendId);
        } else {
            throw new NotFoundException("пользователь не найден");
        }
    }

    @Override
    public List<Long> getMutualFriends(Long id, Long otherId) {
        if (checkFriendsIds(id, otherId)) {
            Set<Long> friends = users.get(id).getFriends();
            friends.retainAll(users.get(otherId).getFriends());
            return new ArrayList<>(friends);
        }
        throw new NotFoundException("пользователь не найден");
    }

    private boolean userExistsByEmail(User user) {
        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean userExistsById(Long id) {
        return users.containsKey(id);
    }

    private boolean checkFriendsIds(Long id, Long friendId) {
        return !Objects.equals(id, friendId)
                && (userExistsById(id) && userExistsById(friendId));
    }

    private boolean userContainsFriend(Long id, Long friendId) {
        return users.get(id).getFriends().contains(friendId);
    }

    private boolean compareIdsForUpdate(Long existingUserId, Long pathId) {
        return userExistsById(pathId) && Objects.equals(existingUserId, pathId);
    }
}
