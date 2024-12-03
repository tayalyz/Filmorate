package ru.company.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.company.filmorate.model.Genre;
import ru.company.filmorate.storage.genreStorage.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class})
public class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    public void testFindGenreById_NotFound() {
        Optional<Genre> genreOptional = genreDbStorage.findById(99L);
        assertThat(genreOptional).isNotPresent();
    }

    @Test
    public void testFindGenreById() {
        Optional<Genre> genreOptional = genreDbStorage.findById(1L);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = genreDbStorage.getAll();
        assertThat(genres).isNotNull();
        assertThat(genres).size().isEqualTo(6);
    }
}
