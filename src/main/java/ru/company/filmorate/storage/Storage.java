package ru.company.filmorate.storage;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {

    T add(T object);

    T update(T object);

    List<T> getAll();

    void deleteAll();

    Optional<T> findById(Long id);
}
