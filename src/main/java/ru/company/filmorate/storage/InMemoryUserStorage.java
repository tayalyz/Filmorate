package ru.company.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.company.filmorate.model.User;
import ru.company.filmorate.util.Identifier;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage<T extends User> implements UserStorage<T> {

    private final Map<Long, T> users = new HashMap<>();

    @Override
    public T add(T user) {
        user.setId(Identifier.INSTANCE.generate(User.class));
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public T update(T user) {
        users.remove(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteAll() {
        users.clear();
        Identifier.INSTANCE.clear(User.class);
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        User sourceUser = users.get(id);
        User targetUser = users.get(friendId);

        sourceUser.getFriends().add(friendId);
        targetUser.getFriends().add(id);
        return targetUser;
    }

    @Override
    public List<User> getFriends(Long id) {
        List<User> friends = new ArrayList<>();
        users.get(id).getFriends().forEach(friendId -> friends.add(users.get(friendId)));
        return friends;
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        User user = users.get(id);
        User friend = users.get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }
}
