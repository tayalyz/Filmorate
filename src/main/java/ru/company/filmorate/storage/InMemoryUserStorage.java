package ru.company.filmorate.storage;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.company.filmorate.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
@Data
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public boolean userExists(User user) {
        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }

    @Override
    public User updateUser(User user, Integer id) {
        return users.put(id, user);
    }

    @Override
    public boolean compareIdsForUpdate(Integer existingUserId, Integer pathId) {
        if (users.containsKey(pathId) && Objects.equals(existingUserId, pathId)) {
            users.remove(pathId);
            return true;
        }
        return false;
    }
}
