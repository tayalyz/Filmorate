package ru.company.filmorate.storage.genreStorage;

import ru.company.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    List<Genre> getAll();

    Optional<Genre> findById(Long id);
}
