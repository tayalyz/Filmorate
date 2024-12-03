package ru.company.filmorate.storage.genreStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.company.filmorate.mapper.GenreMapper;
import ru.company.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public Optional<Genre> findById(Long id) {
        String sql = "SELECT * FROM GENRES WHERE id = ?";

        List<Genre> genre = jdbcTemplate.query(sql, new GenreMapper(), id);
        return genre.size() == 1 ? Optional.of(genre.get(0)) : Optional.empty();
    }
}
