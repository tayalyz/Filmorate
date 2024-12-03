package ru.company.filmorate.storage.mpaStorage;

import ru.company.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaRatingStorage {

    List<MpaRating> getAll();

    Optional<MpaRating> findById(Long id);
}
