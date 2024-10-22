package ru.company.filmorate.storage;

import ru.company.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film, Integer id);

    List<Film> getAllFilms();

    void deleteAllFilms();

    boolean compareIdsForUpdate(Integer existingFilmId, Integer pathId);
}
