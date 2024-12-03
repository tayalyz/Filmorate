package ru.company.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.company.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaRatingMapper implements RowMapper<MpaRating> {

    @Override
    public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
