package ru.company.filmorate.storage;

import ru.company.filmorate.model.Film;

public interface FilmStorage<T extends Film> extends Storage<T> {

    void likeFilm(Long id, Long userId);

    void deleteLike(Long id, Long userId);
}
