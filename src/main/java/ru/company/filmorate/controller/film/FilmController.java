package ru.company.filmorate.controller.film;


import org.springframework.web.bind.annotation.*;
import ru.company.filmorate.controller.Controllers;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.model.Genre;

import java.util.List;

@RequestMapping("/films")
public interface FilmController extends Controllers<Film> {

    @PutMapping("/{id}/like/{userId}")
    void likeFilm(@PathVariable Long id, @PathVariable Long userId);

    @DeleteMapping("/{id}/like/{userId}")
    void deleteLike(@PathVariable Long id, @PathVariable Long userId);

    @GetMapping("/popular")
    List<Film> getPopularFilms(@RequestParam(required = false, value = "count", defaultValue = "10") Integer count);

    @GetMapping("/{id}/genres")
    List<Genre> getGenresByFilmId(@PathVariable Long id);
}
