package ru.company.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.User;
import ru.company.filmorate.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.company.filmorate.FilmorateApplication.log;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users;

    public UserController() {
        this.users = new HashMap<>();
    }

    @PostMapping()
    public User addUser(@RequestBody @Valid User user) {
        user.setId(Identifier.INSTANCE.generate(User.class));
        users.put(user.getId(), user);
        log.info("добавлен пользователь с id {}", user.getId());
        return user;
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestBody @Valid User updatedUser, @PathVariable Integer id) {
        if (users.containsKey(id) && updatedUser.getId().equals(id)) {
            users.remove(id);
        } else {
            throw new NotFoundException("пользователь не найден");
        }

        users.put(updatedUser.getId(), updatedUser);
        log.info("обновлен пользователь с id {}", updatedUser.getId());
        return updatedUser;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @DeleteMapping
    public void deleteAllUsers() {
        users.clear();
        Identifier.INSTANCE.clear(User.class);
        log.info("все пользователи удалены");
    }
}
