package ru.company.filmorate.storage.userStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
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
    public Boolean addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO USER_FRIENDS (user_id, friend_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, userId, friendId) == 1;
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
                    u.birthday
                FROM
                    users u
                GROUP BY
                    u.id, u.email, u.login, u.name, u.birthday
                """;
        String friendsSql = "SELECT friend_id FROM user_friends WHERE user_id = ?";

        List<User> users = jdbcTemplate.query(sql, new UserRowMapper());
        if (!users.isEmpty()) {
            users.forEach(u -> u.setFriends(new HashSet<>(jdbcTemplate.queryForList(friendsSql, Long.class, u.getId()))));
        }
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = """
                SELECT
                    u.id,
                    u.email,
                    u.login,
                    u.name,
                    u.birthday
                FROM
                    users u
                WHERE
                    u.id = ?
                GROUP BY
                    u.id, u.email, u.login, u.name, u.birthday;
                """;
        String friendsSql = "SELECT friend_id FROM user_friends WHERE user_id = ?";

        Optional<User> user = jdbcTemplate.query(sql, new UserRowMapper(), id).stream().findFirst();

        user.ifPresent(u -> {
            List<Long> friendIds = jdbcTemplate.queryForList(friendsSql, Long.class, id);
            u.setFriends(new HashSet<>(friendIds));
        });
        return user;
    }
}
