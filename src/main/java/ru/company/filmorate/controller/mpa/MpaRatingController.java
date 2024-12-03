package ru.company.filmorate.controller.mpa;

import org.springframework.web.bind.annotation.RequestMapping;
import ru.company.filmorate.controller.Controllers;
import ru.company.filmorate.model.MpaRating;

@RequestMapping("/mpa")
public interface MpaRatingController extends Controllers<MpaRating> {
}
