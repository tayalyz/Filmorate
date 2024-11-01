package ru.company.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.company.filmorate.exception.NotFoundException;
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
        return Optional.ofNullable(users.get(id)); // TODO тут бросать исключение?
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        findById(id).orElseThrow(()->new NotFoundException((""))).getFriends().add(friendId); // TODO нужно?
        users.get(friendId).getFriends().add(id);
    }

    @Override
    public List<Long> getFriends(Long id) {
        return new ArrayList<>(users.get(id).getFriends());
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);
    }
}
