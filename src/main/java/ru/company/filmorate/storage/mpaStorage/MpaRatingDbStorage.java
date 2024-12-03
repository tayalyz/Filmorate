package ru.company.filmorate.storage.mpaStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.company.filmorate.mapper.MpaRatingMapper;
import ru.company.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaRatingDbStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getAll() {
        String sql = "SELECT * FROM MPA_RATING";
        return jdbcTemplate.query(sql, new MpaRatingMapper());
    }

    @Override
    public Optional<MpaRating> findById(Long id) {
        String sql = "SELECT * FROM MPA_RATING WHERE id = ?";

        List<MpaRating> rating = jdbcTemplate.query(sql, new MpaRatingMapper(), id);
        return rating.size() == 1 ? Optional.of(rating.get(0)) : Optional.empty();
    }
}
