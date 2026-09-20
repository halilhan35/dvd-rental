package com.halil.dvdrental.mapper;

import com.halil.dvdrental.dto.FilmDTO;
import com.halil.dvdrental.entity.Actor;
import com.halil.dvdrental.entity.Category;
import com.halil.dvdrental.entity.Film;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class FilmMapper {

    public static FilmDTO toDTO(Film film) {

        FilmDTO dto = new FilmDTO();

        dto.setFilmId(film.getFilmId());
        dto.setTitle(film.getTitle());
        dto.setDescription(film.getDescription());
        dto.setReleaseYear(film.getReleaseYear());
        dto.setRentalRate(film.getRentalRate());
        dto.setLength(film.getLength());

        if (film.getLanguage() != null) {
            dto.setLanguageId(film.getLanguage().getLanguageId());
            dto.setLanguageName(film.getLanguage().getName());
        }

        if (film.getActors() != null) {
            dto.setActorIds(
                    film.getActors()
                            .stream()
                            .map(Actor::getActorId)
                            .collect(Collectors.toCollection(LinkedHashSet::new))
            );
        }

        if (film.getCategories() != null) {
            dto.setCategoryIds(
                    film.getCategories()
                            .stream()
                            .map(Category::getCategoryId)
                            .collect(Collectors.toCollection(LinkedHashSet::new))
            );
        }

        return dto;
    }

    // Sadece YENİ film oluştururken çağrılır — filmId her zaman null'dur, bu yüzden set edilmez
    public static Film toEntity(FilmDTO dto) {

        Film film = new Film();

        film.setTitle(dto.getTitle());
        film.setDescription(dto.getDescription());
        film.setReleaseYear(dto.getReleaseYear());
        film.setRentalRate(dto.getRentalRate());
        film.setLength(dto.getLength());

        return film;
    }

    public static FilmDTO toListDTO(Film film) {

        FilmDTO dto = new FilmDTO();

        dto.setFilmId(film.getFilmId());
        dto.setTitle(film.getTitle());
        dto.setDescription(film.getDescription());
        dto.setReleaseYear(film.getReleaseYear());
        dto.setRentalRate(film.getRentalRate());
        dto.setLength(film.getLength());

        if (film.getLanguage() != null) {
            dto.setLanguageId(film.getLanguage().getLanguageId());
            dto.setLanguageName(film.getLanguage().getName());
        }

        return dto;
    }
}