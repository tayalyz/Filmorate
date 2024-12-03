package ru.company.filmorate.controller.genres;

import org.springframework.web.bind.annotation.RequestMapping;
import ru.company.filmorate.controller.Controllers;
import ru.company.filmorate.model.Genre;

@RequestMapping("/genres")
public interface GenreController extends Controllers<Genre> {
}
