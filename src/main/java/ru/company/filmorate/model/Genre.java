package ru.company.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@EqualsAndHashCode
public class Genre {

    private Long id;

    @Size(max = 30, message = "Название жанра должно быть короче 50 символов")
    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;
}
