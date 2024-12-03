package ru.company.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.company.filmorate.exception.NotFoundException;
import ru.company.filmorate.model.MpaRating;
import ru.company.filmorate.storage.mpaStorage.MpaRatingDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaRatingService {

    private final MpaRatingDbStorage mpaRatingDbStorage;

    public List<MpaRating> getAllRatings() {
        return mpaRatingDbStorage.getAll();
    }

    public MpaRating getMpaRatingById(Long id) {
        return mpaRatingDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("рейтинг с id " + id + " не найден"));
    }
}
