package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FilmRepository
        extends JpaRepository<Film, Integer>,
        QuerydslPredicateExecutor<Film> {

    @Query(value = """
            SELECT COUNT(*)
            FROM inventory
            WHERE film_id = :filmId
            """, nativeQuery = true)
    long countInventoryByFilmId(@Param("filmId") Integer filmId);
}