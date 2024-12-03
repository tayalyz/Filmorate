package ru.company.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.company.filmorate.model.MpaRating;
import ru.company.filmorate.storage.mpaStorage.MpaRatingDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRatingDbStorage.class})
public class MpaRatingDbStorageTest {

    private final MpaRatingDbStorage mpaRatingDbStorage;

    @Test
    public void testFindMpaById_NotFound() {
        Optional<MpaRating> genreOptional = mpaRatingDbStorage.findById(99L);
        assertThat(genreOptional).isNotPresent();
    }

    @Test
    public void testFindMpaById() {
        Optional<MpaRating> genreOptional = mpaRatingDbStorage.findById(1L);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetAllMpaRating() {
        List<MpaRating> genres = mpaRatingDbStorage.getAll();
        assertThat(genres).isNotNull().size().isEqualTo(5);
    }
}
