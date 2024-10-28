package ru.company.filmorate.storage;

import ru.company.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> addFilm(Film film);

    Optional<Film> updateFilm(Film film, Long id);

    List<Film> getAllFilms();

    void deleteAllFilms();

    void likeFilm(Long id, Long userId);

    boolean filmExistsById(Long id);

    void deleteLike(Long id, Long userId);

    List<Film> getPopularFilms(Integer count);
}
