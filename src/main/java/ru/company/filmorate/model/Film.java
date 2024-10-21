package ru.company.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.company.filmorate.annotation.ReleaseDateValidation;

import java.time.LocalDate;

@Data
@Builder
public class Film {

    private Integer id;

    @Size(max = 30, message = "Название фильма должно быть короче 50 символов")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма слишком длинное")
    private String description;

    @ReleaseDateValidation(dateStart = "1895.12.28", message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;
}
