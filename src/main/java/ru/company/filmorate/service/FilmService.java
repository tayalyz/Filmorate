package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.storage.FilmStorage;
import ru.company.filmorate.util.Identifier;

import java.util.List;

import static ru.company.filmorate.FilmorateApplication.log;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public Film addFilm(Film film) {
        film.setId(Identifier.INSTANCE.generate(Film.class));
        filmStorage.addFilm(film);
        log.info("добавлен фильм с id {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film, Integer id) {
        if (filmStorage.compareIdsForUpdate(film.getId(), id)) {
            filmStorage.updateFilm(film, id);
            log.info("обновлен фильм с id {}", id);
            return film;
        }
        throw new NotFoundException("фильм не найден");
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
        Identifier.INSTANCE.clear(Film.class);
        log.info("все фильмы удалены");
    }
}
