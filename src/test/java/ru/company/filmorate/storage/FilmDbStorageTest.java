package ru.company.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.company.filmorate.model.Film;
import ru.company.filmorate.model.MpaRating;
import ru.company.filmorate.storage.filmStorage.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
public class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    public void testGetAllFilms() {
        List<Film> films = filmStorage.getAll();
        assertThat(films).isNotNull();
        assertThat(films).size().isEqualTo(3);
    }

    @Test
    public void deleteAllFilms() {
        filmStorage.deleteAll();
        List<Film> films = filmStorage.getAll();
        assertThat(films).isEmpty();
    }

    @Test
    public void testFindFilmById_NotFound() {
        Optional<Film> filmOptional = filmStorage.findById(99L);
        assertThat(filmOptional).isNotPresent();
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.findById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testAdd() {
        Film film = Film.builder().build();
        film.setName("name4");
        film.setDescription("desc4");
        film.setReleaseDate(LocalDate.of(2008, 8, 28));
        film.setDuration(120);
        film.setMpa(MpaRating.builder().build());

        Film addedFilm = filmStorage.add(film);
        assertThat(addedFilm).isNotNull();
        assertThat(filmStorage.getAll()).size().isEqualTo(4);
    }

    @Test
    public void testUpdateFilm() {
        Film film = Film.builder().build();
        film.setName("name4");
        film.setDescription("desc4");
        film.setReleaseDate(LocalDate.of(2008, 8, 28));
        film.setDuration(120);

        Film updatedFilm = filmStorage.update(film);
        assertThat(updatedFilm).isNotNull();
        assertThat(filmStorage.getAll()).size().isEqualTo(3);
    }

    @Test
    public void testLikeFilm() {
        filmStorage.likeFilm(1L, 1L);
        Optional<Film> filmOptional = filmStorage.findById(1L);
        assertThat(filmOptional.get().getLikes()).contains(1L).size().isEqualTo(1);
    }
}
