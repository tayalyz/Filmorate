package ru.company.filmorate.controller.genres;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.company.filmorate.model.Genre;
import ru.company.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreControllerImpl implements GenreController {

    private final GenreService genreService;

    @Override
    public List<Genre> getAll() {
        return genreService.getAllGenres();
    }

    @Override
    public Genre findById(Long id) {
        return genreService.getGenreById(id);
    }
}
