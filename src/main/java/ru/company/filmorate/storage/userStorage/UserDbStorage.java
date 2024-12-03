package ru.company.filmorate.storage.userStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.company.filmorate.mapper.UserMapper;
import ru.company.filmorate.mapper.UserRowMapper;
import ru.company.filmorate.model.User;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO USER_FRIENDS (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);

        return findById(userId).orElse(null); // todo тут дублировать findById?
    }

    @Override
    public List<User> getFriends(Long id) {
        String sql = "SELECT * FROM USERS U JOIN USER_FRIENDS UF on U.ID = UF.FRIEND_ID WHERE UF.USER_ID = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), id);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        String sql = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, id, friendId);
    }

    public User add(User user) {
        String sql = "INSERT INTO USERS (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        setFriends(user);
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where ID = ?";
        long userId = user.getId();

        jdbcTemplate.update("DELETE FROM USER_FRIENDS WHERE USER_FRIENDS.USER_ID = ?", userId);
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        setFriends(user);
        return user;
    }

    private void setFriends(User user) {
        String setFriends = "INSERT INTO USER_FRIENDS (user_id, friend_id) VALUES (?, ?)";

        Set<Long> friends = user.getFriends();
        if (friends != null && !friends.isEmpty()) {
            jdbcTemplate.batchUpdate(setFriends, friends, friends.size(), (ps, friendId) -> {
                ps.setLong(1, user.getId());
                ps.setLong(2, friendId);
            });
        }
    }

    @Override
    public List<User> getAll() {
        String sql = """
                SELECT
                    u.id,
                    u.email,
                    u.login,
                    u.name,
                    u.birthday,
                    GROUP_CONCAT(f.id) AS friends
                FROM
                    users u
                LEFT JOIN
                    user_friends uf ON u.id = uf.user_id
                LEFT JOIN
                    users f ON uf.friend_id = f.id
                GROUP BY
                    u.id, u.email, u.login, u.name, u.birthday;
                """;

        Map<Long, User> users = jdbcTemplate.query(sql, new UserMapper());
        return users != null ? new ArrayList<>(users.values()) : new ArrayList<>();
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM USER_FRIENDS;" +  // todo нужно удалять друзей?
                "DELETE FROM USERS";
        jdbcTemplate.update(sql);
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = """
                SELECT
                    u.id,
                    u.email,
                    u.login,
                    u.name,
                    u.birthday,
                    GROUP_CONCAT(f.id) AS friends
                FROM
                    users u
                LEFT JOIN
                    user_friends uf ON u.id = uf.user_id
                LEFT JOIN
                    users f ON uf.friend_id = f.id
                WHERE
                    u.id = ?
                GROUP BY
                    u.id, u.email, u.login, u.name, u.birthday;
                """;

        Map<Long, User> users = jdbcTemplate.query(sql, new UserMapper(), id);
        return users != null ? Optional.ofNullable(users.get(id)) : Optional.empty();
    }
}
