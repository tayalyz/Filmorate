package ru.company.filmorate.storage.filmStorage;

import ru.company.filmorate.model.Film;
import ru.company.filmorate.model.Genre;
import ru.company.filmorate.storage.Storage;

import java.util.List;

public interface FilmStorage extends Storage<Film> {

    void likeFilm(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    List<Genre> getGenresByFilmId(Long id);
}
