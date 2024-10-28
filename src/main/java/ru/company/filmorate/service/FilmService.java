package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public Optional<Film> addFilm(Film film) {
        filmStorage.addFilm(film);
        log.info("добавлен фильм с id {}", film.getId());
        return Optional.of(film);
    }

    public Optional<Film> updateFilm(Film film, Long id) {
        filmStorage.updateFilm(film, id);
        log.info("обновлен фильм с id {}", id);
        return Optional.of(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
        log.info("все фильмы удалены");
    }

    public void likeFilm(Long id, Long userId) {
        filmStorage.likeFilm(id, userId);
        log.info("пользователь с id {} поставил лайк на фильм с id {}", userId, id);
    }

    public void deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
        log.info("пользователь с id {} удалил лайк с фильма с id {}", userId, id);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }
}
