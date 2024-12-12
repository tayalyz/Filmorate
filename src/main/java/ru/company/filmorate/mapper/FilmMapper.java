package ru.company.filmorate.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.model.Genre;
import ru.company.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FilmMapper implements ResultSetExtractor<Map<Long, Film>> {

    @Override
    public Map<Long, Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> films = new HashMap<>();
        while (rs.next()) {
            long filmId = rs.getLong("id");
            films.putIfAbsent(filmId,
                    Film.builder()
                            .id(filmId)
                            .name(rs.getString("name"))
                            .description(rs.getString("description"))
                            .releaseDate(rs.getDate("release_date").toLocalDate())
                            .duration(rs.getInt("duration"))
                            .mpa(MpaRating.builder()
                                    .id(rs.getLong("mpa_id"))
                                    .name(rs.getString("mpa_name"))
                                    .build())
                            .likes(new HashSet<>())
                            .genres(new LinkedHashSet<>())
                            .build());

            Film film = films.get(filmId);

            long genreId = rs.getLong("genre_id");
            if (genreId != 0) {
                film.getGenres().add(
                        Genre.builder()
                                .id(genreId)
                                .name(rs.getString("genre_name"))
                                .build()
                );
            }

            long likeId = rs.getLong("like_id");
            if (likeId != 0) {
                film.getLikes().add(likeId);
            }

            films.put(filmId, film);
        }
        return films;
    }
}
