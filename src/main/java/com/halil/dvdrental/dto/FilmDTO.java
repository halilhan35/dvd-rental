package com.halil.dvdrental.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class FilmDTO {

    private Integer filmId;
    private String title;
    private String description;
    private Integer releaseYear;
    private Integer languageId;
    private String languageName;
    private BigDecimal rentalRate;
    private Integer length;

    private Set<Integer> actorIds = new LinkedHashSet<>();
    private Set<Integer> categoryIds = new LinkedHashSet<>();
}