package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface FilmRepository
        extends JpaRepository<Film, Integer>,
        QuerydslPredicateExecutor<Film> {
}