package com.halil.dvdrental.service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    private final JPAQueryFactory queryFactory;


    @Autowired
    public FilmService(FilmRepository filmRepository,
                       JPAQueryFactory queryFactory) {
        this.filmRepository = filmRepository;
        this.queryFactory = queryFactory;
    }

    public List<Film> getFilms(int first,
                               int pageSize,
                               String sortField,
                               boolean ascending,
                               String keyword) {

        QFilm film = QFilm.film;

        Order order = ascending ? Order.ASC : Order.DESC;

        OrderSpecifier<?> orderSpecifier;

        if ("title".equals(sortField)) {
            orderSpecifier = new OrderSpecifier<>(order, film.title);
        } else if ("releaseYear".equals(sortField)) {
            orderSpecifier = new OrderSpecifier<>(order, film.releaseYear);
        } else if ("rentalRate".equals(sortField)) {
            orderSpecifier = new OrderSpecifier<>(order, film.rentalRate);
        } else if ("length".equals(sortField)) {
            orderSpecifier = new OrderSpecifier<>(order, film.length);
        } else {
            orderSpecifier = new OrderSpecifier<>(Order.ASC, film.filmId);
        }

        BooleanExpression predicate = null;

        if (keyword != null && !keyword.trim().isEmpty()) {
            predicate = film.title.containsIgnoreCase(keyword.trim());
        }

        return queryFactory
                .selectFrom(film)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(first)
                .limit(pageSize)
                .fetch();
    }

    public long countFilms() {

        QFilm film = QFilm.film;

        return queryFactory
                .select(film.count())
                .from(film)
                .fetchOne();
    }

    public long countFilms(String keyword) {

        QFilm film = QFilm.film;

        BooleanExpression predicate = null;

        if (keyword != null && !keyword.trim().isEmpty()) {
            predicate = film.title.containsIgnoreCase(keyword.trim());
        }

        return queryFactory
                .select(film.count())
                .from(film)
                .where(predicate)
                .fetchOne();
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