package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.company.filmorate.exception.BadRequestException;
import ru.company.filmorate.exception.DuplicateUserException;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.model.Genre;
import ru.company.filmorate.storage.filmStorage.FilmStorage;
import ru.company.filmorate.storage.genreStorage.GenreStorage;
import ru.company.filmorate.storage.mpaStorage.MpaRatingStorage;
import ru.company.filmorate.storage.userStorage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final MpaRatingStorage mpaRatingStorage;

    private final GenreStorage genreStorage;

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("фильм с id " + id + " не найден"));
    }

    public Film addFilm(Film film) {
        validateFilm(film);

        filmStorage.add(film);
        log.info("добавлен фильм с id {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        Optional<Film> optionalFilm = filmStorage.findById(film.getId());

        if (optionalFilm.isEmpty()) {
            log.info("фильм с id {} не найден", film.getId());
            throw new NotFoundException("фильм не найден");
        }

        validateFilm(film);

        filmStorage.update(film);
        log.info("обновлен фильм с id {}", film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
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

    public List<Genre> getGenresByFilmId(Long id) {
        return filmStorage.getGenresByFilmId(id);
    }

    private boolean filmContainsLike(Long id, Long userId) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("фильм не найден"))
                .getLikes().contains(userId);
    }

    private void validateFilm(Film film) {
        Long mpaRatingId = film.getMpa().getId();
        if (mpaRatingId != null) {
            mpaRatingStorage.findById(mpaRatingId)
                    .orElseThrow(() -> new BadRequestException("рейтинг с id " + mpaRatingId+ " не найден"));
        }

        LinkedHashSet<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> genreStorage.findById(genre.getId())
                    .orElseThrow(() -> new BadRequestException("рейтинг с id " + genre.getId() + " не найден")));
        }

        Set<Long> likes = film.getLikes();
        if (likes != null && !likes.isEmpty()) {
            likes.forEach(likeUser -> userStorage.findById(likeUser)
                    .orElseThrow(() -> new BadRequestException("пользователь с id " + likeUser + " не найден")));
        }
    }
}
