package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.company.filmorate.exception.DuplicateUserException;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.model.User;
import ru.company.filmorate.storage.FilmStorage;
import ru.company.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage<Film> filmStorage;

    private final UserStorage<User> userStorage;

    public Film addFilm(Film film) {
        filmStorage.add(film);
        log.info("добавлен фильм с id {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        if (filmStorage.findById(film.getId()).isEmpty()) {
            log.info("фильм с id {} не найден", film.getId());
            throw new NotFoundException("фильм не найден");
        }
        filmStorage.update(film);
        log.info("обновлен фильм с id {}", film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public void deleteAllFilms() {
        filmStorage.deleteAll();
        log.info("все фильмы удалены");
    }

    public void likeFilm(Long id, Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.info("пользователь с id {} не найден", userId);
            throw new NotFoundException("пользователь не найден");
        }
        if (filmContainsLike(id, userId)) {
            log.info("пользователь с id {} уже поставил лайк на фильм с id {}", userId, id);
            throw new DuplicateUserException("лайк уже поставлен");
        }
        filmStorage.likeFilm(id, userId);
        log.info("пользователь с id {} поставил лайк на фильм с id {}", userId, id);
    }

    public void deleteLike(Long id, Long userId) {
        if (!filmContainsLike(id, userId)) {
            log.info("лайк на фильме с id {} от пользователя с id {} не найден", id, userId);
            throw new NotFoundException("лайк не найден");
        }
        filmStorage.deleteLike(id, userId);
        log.info("пользователь с id {} удалил лайк с фильма с id {}", userId, id);
    }

    public List<Film> getPopularFilms(Integer count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean filmContainsLike(Long id, Long userId) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("фильм не найден"))
                .getLikes().contains(userId);
    }
}
