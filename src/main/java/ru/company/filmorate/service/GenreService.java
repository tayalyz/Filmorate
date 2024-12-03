package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.Genre;
import ru.company.filmorate.storage.genreStorage.GenreDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAll();
    }

    public Genre getGenreById(Long id) {
        return genreDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("жанр с id " + id + " не найден"));
    }
}
