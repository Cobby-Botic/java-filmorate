package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {

    private Long id;

    @NotBlank
    private String name;

    private LocalDate releaseDate;

    @Min(1)
    private Integer duration;

    private Set<Genre> genres;

    private Mpa mpa;

    @NotBlank
    @Length(max = 200)
    private String description;

    private Set<Integer> likes;
}
