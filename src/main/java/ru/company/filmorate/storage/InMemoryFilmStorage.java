package ru.company.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.company.filmorate.model.Film;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        return films.put(film.getId(), film);
    }

    @Override
    public Film updateFilm(Film film, Integer id) {
        return films.put(id, film);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
    }

    @Override
    public boolean compareIdsForUpdate(Integer existingFilmId, Integer pathId) {
        if (films.containsKey(pathId) && Objects.equals(existingFilmId, pathId)) {
            films.remove(pathId);
            return true;
        }
        return false;
    }
}
