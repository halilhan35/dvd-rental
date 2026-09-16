package com.halil.dvdrental.service;

import com.halil.dvdrental.entity.Film;
import com.halil.dvdrental.entity.QFilm;
import com.halil.dvdrental.repository.FilmRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {

    private final FilmRepository filmRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Optional<Film> getFilmById(Integer id) {
        return filmRepository.findById(id);
    }

    public Film saveFilm(Film film) {
        return filmRepository.save(film);
    }

    public void deleteFilm(Integer id) {
        filmRepository.deleteById(id);
    }

    public List<Film> searchFilms(String keyword) {

        QFilm film = QFilm.film;

        BooleanExpression predicate =
                film.title.containsIgnoreCase(keyword);

        return StreamSupport
                .stream(filmRepository.findAll(predicate).spliterator(), false)
                .toList();
    }
}