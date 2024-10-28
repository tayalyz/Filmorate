package ru.company.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private final InMemoryUserStorage userStorage;

    @Override
    public Optional<Film> addFilm(Film film) {
        film.setId(Identifier.INSTANCE.generate(Film.class));
        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateFilm(Film film, Long id) {
        if (compareIdsForUpdate(film.getId(), id)) {
            films.remove(id);
            films.put(id, film);
            return Optional.of(film);
        }
        throw new NotFoundException("фильм не найден");
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
        Identifier.INSTANCE.clear(Film.class);
    }

    @Override
    public void likeFilm(Long id, Long userId) {
        if (userStorage.userExistsById(userId) && filmExistsById(id)) {
            films.get(id).getLikes().add(userId);
        } else {
            throw new NotFoundException("пользователь/фильм не найден");
        }
    }

    @Override
    public boolean filmExistsById(Long id) {
        return films.containsKey(id);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        if (userStorage.userExistsById(userId) && filmExistsById(id)) {
            if (filmContainsLike(id, userId)) {
                films.get(id).getLikes().remove(userId);
            } else {
                throw new NotFoundException("лайк не найден");
            }
        } else {
            throw new NotFoundException("пользователь/фильм не найден");
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return films.values().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean filmContainsLike(Long id, Long userId) {
        return films.get(id).getLikes().contains(userId);
    }

    private boolean compareIdsForUpdate(Long existingFilmId, Long pathId) {
        return films.containsKey(pathId) && Objects.equals(existingFilmId, pathId);
    }
}
