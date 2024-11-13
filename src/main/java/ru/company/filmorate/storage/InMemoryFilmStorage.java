package ru.company.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.util.Identifier;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage<T extends Film> implements FilmStorage<T> {

    private final Map<Long, T> films = new HashMap<>();

    @Override
    public T add(T film) {
        film.setId(Identifier.INSTANCE.generate(Film.class));
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public T update(T film) {
        films.remove(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteAll() {
        films.clear();
        Identifier.INSTANCE.clear(Film.class);
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void likeFilm(Long id, Long userId) {
        films.get(id).getLikes().add(userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        films.get(id).getLikes().remove(userId);
    }
}
