package ru.company.filmorate.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.company.filmorate.model.User;
import ru.company.filmorate.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController{

    private final UserService userService;

    @Override
    public User findById(Long id) {
        return userService.getUserById(id);
    }

    @Override
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        return userService.getFriends(id);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @Override
    public List<User> getMutualFriends(Long id, Long otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    @Override
    public User create(User user) {
        return userService.addUser(user);
    }

    @Override
    public User update(User user) {
        return userService.updateUser(user);
    }

    @Override
    public List<User> getAll() {
        return userService.getAllUsers();
    }
}
