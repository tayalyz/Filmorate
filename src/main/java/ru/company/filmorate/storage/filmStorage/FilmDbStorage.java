package ru.company.filmorate.storage.filmStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.company.filmorate.mapper.FilmMapper;
import ru.company.filmorate.mapper.GenreMapper;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.model.Genre;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void likeFilm(Long id, Long userId) {
        String sql = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        String sql = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public Film add(Film film) {
        String setFilm = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(setFilm, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long filmId = (Long) keyHolder.getKey();
        film.setId(filmId);

        setLikesAndGenres(film, true);
        return film;
    }

    @Override
    public Film update(Film film) {
        String setFilm = "UPDATE FILMS SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";

        Long filmId = film.getId();

        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ?", filmId);
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);

        jdbcTemplate.update(setFilm, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), filmId);

        setLikesAndGenres(film, false);
        return film;
    }

    @Override
    public List<Film> getAll() {
        String sql = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    r.name AS mpa_name,
                    r.id AS mpa_id,
                    fg.genre_id,
                    g.name AS genre_name,
                    fl.user_id AS like_id
                FROM
                    films f
                LEFT JOIN
                    film_likes fl on f.id = fl.film_id
                LEFT JOIN
                    film_genres fg on f.id = fg.film_id
                LEFT JOIN
                    genres g on fg.genre_id = g.id
                LEFT JOIN
                    mpa_rating r ON f.mpa_id = r.id;
                """;

        Map<Long, Film> films = jdbcTemplate.query(sql, new FilmMapper());
        return films != null ? new ArrayList<>(films.values()) : new ArrayList<>();
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    r.name AS mpa_name,
                    r.id AS mpa_id,
                    fg.genre_id,
                    g.name AS genre_name,
                    fl.user_id AS like_id
                FROM
                    films f
                LEFT JOIN
                    film_likes fl on f.id = fl.film_id
                LEFT JOIN
                    film_genres fg on f.id = fg.film_id
                LEFT JOIN
                    genres g on fg.genre_id = g.id
                LEFT JOIN
                    mpa_rating r ON f.mpa_id = r.id
                WHERE
                    f.id = ?;
                """;

        Map<Long, Film> films = jdbcTemplate.query(sql, new FilmMapper(), id);
        return films != null ? Optional.ofNullable(films.get(id)) : Optional.empty();
    }

    @Override
    public List<Genre> getGenresByFilmId(Long id) {
        String sql = "SELECT * FROM GENRES G JOIN FILM_GENRES FG ON G.ID = FG.GENRE_ID WHERE FG.FILM_ID = ?";
        return jdbcTemplate.query(sql, new GenreMapper(), id);
    }

    private void setLikesAndGenres(Film film, boolean nonAddable) {
        String setGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        String setLikes = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";

        LinkedHashSet<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            jdbcTemplate.batchUpdate(setGenres, genres, genres.size(), (ps, genre) -> {
                ps.setLong(1, film.getId());
                ps.setLong(2, genre.getId());
            });
        }

        if (!nonAddable) {
            Set<Long> likes = film.getLikes();
            if (likes != null && !likes.isEmpty()) {
                jdbcTemplate.batchUpdate(setLikes, likes, likes.size(), (ps, userId) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, userId);
                });
            }
        }
    }
}