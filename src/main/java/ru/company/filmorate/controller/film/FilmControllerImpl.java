package ru.company.filmorate.controller.film;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.model.Genre;
import ru.company.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilmControllerImpl implements FilmController {

    private final FilmService filmService;

    @Override
    public Film findById(Long id) {
        return filmService.findById(id);
    }

    @Override
    public void deleteAllFilms() {
        filmService.deleteAllFilms();
    }

    @Override
    public void likeFilm(Long id, Long userId) {
        filmService.likeFilm(id, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        filmService.deleteLike(id, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return filmService.getPopularFilms(count);
    }

    @Override
    public List<Genre> getGenresByFilmId(Long id) {
        return filmService.getGenresByFilmId(id);
    }

    @Override
    public Film create(Film film) {
        return filmService.addFilm(film);
    }

    @Override
    public Film update(Film updatedFilm) {
        return filmService.updateFilm(updatedFilm);
    }

    @Override
    public List<Film> getAll() {
        return filmService.getAllFilms();
    }
}
