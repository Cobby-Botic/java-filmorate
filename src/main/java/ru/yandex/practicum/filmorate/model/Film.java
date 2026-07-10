package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class Film {
    private Integer id;
    private Set<Integer> likes;

    @NotBlank
    private String name;

    @NotBlank
    @Length(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Min(1)
    private Integer duration;
}
