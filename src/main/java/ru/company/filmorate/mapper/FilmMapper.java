package ru.company.filmorate.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.model.Genre;
import ru.company.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.*;
// RowMapper

public class FilmMapper implements ResultSetExtractor<Map<Long, Film>> {
    // TODO переписать на putIfAbsent
    @Override
    public Map<Long, Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> films = new HashMap<>();
        while (rs.next()) {
            long filmId = rs.getLong("id");
            Film film = films.computeIfAbsent(filmId, k -> Film.builder().id(filmId).build());

            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(MpaRating.builder()
                    .id(rs.getLong("mpa_id"))
                    .name(rs.getString("mpa_name"))
                    .build());

            String likesString = rs.getString("likes");
            if (likesString != null) {
                Arrays.stream(likesString.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .forEach(film.getLikes()::add);
            }

//            long genreId = rs.getLong("genre_id");
//            if(genreId != 0){
//                films.get(filmId).getGenres().add(
//                        Genre.builder()
//                                .id(genreId)
//                                .name(rs.getString("genre_name"))
//                                .build()
//                );
//            }

            String genresCombined = rs.getString("genres_combined");
            if (genresCombined != null) {
                for (String genrePair : genresCombined.split(",")) {
                    String[] parts = genrePair.trim().split(":");
                    if (parts.length == 2) {
                        try {
                            long genreId = Long.parseLong(parts[0]);
                            String genreName = parts[1];
                            film.getGenres().add(Genre.builder()
                                    .id(genreId)
                                    .name(genreName)
                                    .build());
                        } catch (NumberFormatException e) {
                            throw new SQLDataException("Error parsing genre ID: " + e.getMessage());
                        }
                    }
                }
            }

            films.put(filmId, film);
        }
        return films;
    }
}
