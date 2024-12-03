package ru.company.filmorate.controller.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.company.filmorate.model.MpaRating;
import ru.company.filmorate.service.MpaRatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MpaRatingControllerImpl implements MpaRatingController {

    private final MpaRatingService mpaRatingService;

    @Override
    public List<MpaRating> getAll() {
        return mpaRatingService.getAllRatings();
    }

    @Override
    public MpaRating findById(Long id) {
        return mpaRatingService.getMpaRatingById(id);
    }
}
