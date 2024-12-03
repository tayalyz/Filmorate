package ru.company.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/default")
public interface Controllers<T> {

    @PostMapping
    default T create(@Valid @RequestBody T obj) {
        throw new UnsupportedOperationException("Метод не определен");
    }

    @PutMapping
    default T update(@Valid @RequestBody T obj) {
        throw new UnsupportedOperationException("Метод не определен");
    }

    @GetMapping("/{id}")
    T findById(@PathVariable("id") Long id);

    @GetMapping
    List<T> getAll();
}
