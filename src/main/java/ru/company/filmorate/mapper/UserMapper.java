package ru.company.filmorate.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.company.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserMapper implements ResultSetExtractor<Map<Long, User>> {

    @Override
    public Map<Long, User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, User> users = new HashMap<>();
        while (rs.next()) {
            long userId = rs.getLong("id");
            User user = users.computeIfAbsent(userId, k -> {
                try {
                    return User.builder().id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .login(rs.getString("login"))
                            .email(rs.getString("email"))
                            .birthday(rs.getDate("birthday").toLocalDate())
                            .build();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            String friendsString = rs.getString("friends");
            if (friendsString != null) {
                Arrays.stream(friendsString.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .forEach(user.getFriends()::add);
            }
            users.put(userId, user);
        }
        return users;
    }
}