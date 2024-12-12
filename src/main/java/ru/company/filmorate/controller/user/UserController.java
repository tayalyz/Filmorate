package ru.company.filmorate.controller.user;

import org.springframework.web.bind.annotation.*;
import ru.company.filmorate.controller.Controllers;
import ru.company.filmorate.model.User;

import java.util.List;

@RequestMapping("/users")
public interface UserController extends Controllers<User> {

    @PutMapping("/{id}/friends/{friendId}")
    User addFriend(@PathVariable Long id, @PathVariable Long friendId);

    @GetMapping("{id}/friends")
    List<User> getFriends(@PathVariable Long id);

    @DeleteMapping("{id}/friends/{friendId}")
    void deleteFriend(@PathVariable Long id, @PathVariable Long friendId);

    @GetMapping("/{id}/friends/common/{otherId}")
    List<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId);
}
