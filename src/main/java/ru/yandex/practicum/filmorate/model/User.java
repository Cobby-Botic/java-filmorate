package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class User {
    private Integer id;

    @NotBlank
    private String login;

    @Email
    @NotBlank
    private String email;
    private String name;

    @PastOrPresent
    private LocalDate birthday;

    private Set<Integer> friends;
}
