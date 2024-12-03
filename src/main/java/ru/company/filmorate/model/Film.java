package ru.company.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.company.filmorate.annotation.ReleaseDateValidation;

import java.time.LocalDate;
import java.util.*;

@Setter
@Getter
@Builder
@EqualsAndHashCode
public class Film {

    private Long id;

    @Size(max = 50, message = "Название фильма должно быть короче 50 символов")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма слишком длинное")
    private String description;

    @ReleaseDateValidation(dateStart = "1895.12.28", message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @Builder.Default
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    @NotNull
    private MpaRating mpa;
}
