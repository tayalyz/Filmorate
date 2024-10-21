package ru.company.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ru.company.filmorate.FilmorateApplication.log;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films;

    public FilmController() {
        this.films = new HashMap<>();
    }

    @PostMapping()
    public Film addFilm(@RequestBody @Valid Film film) {
        film.setId(Identifier.INSTANCE.generate(Film.class));
        films.put(film.getId(), film);
        log.info("добавлен фильм с id {}", film.getId());
        return film;
    }

    @PutMapping("/{id}")
    public Film updateFilms(@RequestBody @Valid Film updatedFilm, @PathVariable Integer id) {
        if (films.containsKey(id) && updatedFilm.getId().equals(id)) {
            films.remove(id);
        } else {
            throw new NotFoundException("фильм не найден");
        }

        films.put(updatedFilm.getId(), updatedFilm);
        log.info("обновлен фильм с id {}", updatedFilm.getId());
        return updatedFilm;
    }

    @GetMapping
    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @DeleteMapping
    public void deleteAllFilms() {
        films.clear();
        Identifier.INSTANCE.clear(Film.class);
        log.info("все фильмы удалены");
    }
}
