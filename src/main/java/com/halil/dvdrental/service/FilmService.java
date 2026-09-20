package com.halil.dvdrental.service;

import com.halil.dvdrental.entity.Actor;
import com.halil.dvdrental.entity.Film;
import com.halil.dvdrental.entity.QFilm;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.halil.dvdrental.entity.Category;
import com.halil.dvdrental.repository.ActorRepository;
import com.halil.dvdrental.repository.CategoryRepository;
import com.halil.dvdrental.repository.FilmRepository;

import java.util.*;
import java.util.stream.StreamSupport;
import com.halil.dvdrental.dto.FilmDTO;
import com.halil.dvdrental.mapper.FilmMapper;

@Service
@Transactional
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final JPAQueryFactory queryFactory;
    private final ActorRepository actorRepository;
    private final CategoryRepository categoryRepository;
    private final LanguageService languageService;


    public List<Film> getFilms(int first,
                               int pageSize,
                               String sortField,
                               boolean ascending,
                               String keyword) {

        QFilm film = QFilm.film;

        Order order = ascending ? Order.ASC : Order.DESC;

        OrderSpecifier<?> orderSpecifier;

        if ("title".equals(sortField)) {

            orderSpecifier =
                    new OrderSpecifier<>(order, film.title);

        } else if ("releaseYear".equals(sortField)) {

            orderSpecifier =
                    new OrderSpecifier<>(order, film.releaseYear);

        } else if ("rentalRate".equals(sortField)) {

            orderSpecifier =
                    new OrderSpecifier<>(order, film.rentalRate);

        } else if ("length".equals(sortField)) {

            orderSpecifier =
                    new OrderSpecifier<>(order, film.length);

        } else {

            orderSpecifier =
                    new OrderSpecifier<>(Order.ASC, film.filmId);
        }

        BooleanExpression predicate = null;

        if (keyword != null && !keyword.trim().isEmpty()) {

            predicate =
                    film.title.containsIgnoreCase(
                            keyword.trim()
                    );
        }

        return queryFactory
                .selectFrom(film)
                .leftJoin(film.language).fetchJoin()
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(first)
                .limit(pageSize)
                .fetch();
    }

    public List<FilmDTO> getFilmDTOs(int first,
                                     int pageSize,
                                     String sortField,
                                     boolean ascending,
                                     String keyword) {

        List<Film> films = getFilms(
                first,
                pageSize,
                sortField,
                ascending,
                keyword
        );

        return films.stream()
                .map(FilmMapper::toListDTO)
                .toList();
    }

    public FilmDTO getFilmDTOById(Integer id) {
        return filmRepository.findById(id)
                .map(FilmMapper::toDTO)
                .orElse(null);
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

            predicate =
                    film.title.containsIgnoreCase(
                            keyword.trim()
                    );
        }

        return queryFactory
                .select(film.count())
                .from(film)
                .where(predicate)
                .fetchOne();
    }


    /*
     * Kaydetme sırasında kullanılır.
     *
     * BURADA collection'ları normal HashSet'e çevirmiyoruz.
     *
     * Hibernate'in kendi PersistentSet'leri korunuyor.
     */
    @Transactional
    public Film getFilmForEdit(Integer filmId) {

        QFilm film = QFilm.film;

        return queryFactory
                .selectFrom(film)
                .leftJoin(film.language).fetchJoin()
                .leftJoin(film.actors).fetchJoin()
                .leftJoin(film.categories).fetchJoin()
                .where(film.filmId.eq(filmId))
                .distinct()
                .fetchOne();
    }

    public List<Film> getAllFilms() {
        QFilm film = QFilm.film;
        return queryFactory
                .selectFrom(film)
                .leftJoin(film.language).fetchJoin()
                .fetch();
    }


    /*
     * Film kaydetme / güncelleme
     */


    public List<FilmDTO> getAllFilmDTOs() {
        return getAllFilms()
                .stream()
                .map(FilmMapper::toDTO)
                .toList();
    }

    public FilmDTO saveFilmDTO(FilmDTO dto) {

        Film film;

        // YENİ FİLM
        if (dto.getFilmId() == null) {

            film = FilmMapper.toEntity(dto);

            if (dto.getLanguageId() != null) {
                languageService
                        .getLanguageById(dto.getLanguageId())
                        .ifPresent(film::setLanguage);
            }

            List<Actor> actors =
                    actorRepository.findAllById(dto.getActorIds());

            List<Category> categories =
                    categoryRepository.findAllById(dto.getCategoryIds());

            film.setActors(new LinkedHashSet<>(actors));
            film.setCategories(new LinkedHashSet<>(categories));

            film = filmRepository.save(film);

        }
        // MEVCUT FİLMİ GÜNCELLE
        else {

            film = getFilmForEdit(dto.getFilmId());

            if (film == null) {
                return null;
            }

            film.setTitle(dto.getTitle());
            film.setDescription(dto.getDescription());
            film.setReleaseYear(dto.getReleaseYear());
            film.setRentalRate(dto.getRentalRate());
            film.setLength(dto.getLength());

            if (dto.getLanguageId() != null) {
                languageService
                        .getLanguageById(dto.getLanguageId())
                        .ifPresent(film::setLanguage);
            } else {
                film.setLanguage(null);
            }

            List<Actor> actors =
                    actorRepository.findAllById(dto.getActorIds());

            film.getActors().clear();
            film.getActors().addAll(actors);

            List<Category> categories =
                    categoryRepository.findAllById(dto.getCategoryIds());

            film.getCategories().clear();
            film.getCategories().addAll(categories);
        }

        return FilmMapper.toDTO(film);
    }

    public boolean deleteFilm(Integer id) {
        long inventoryCount = filmRepository.countInventoryByFilmId(id);

        if (inventoryCount > 0) {
            return false;
        }

        filmRepository.deleteById(id);
        return true;
    }

    public List<Film> searchFilms(String keyword) {

        QFilm film = QFilm.film;

        BooleanExpression predicate =
                film.title.containsIgnoreCase(keyword);

        return StreamSupport
                .stream(
                        filmRepository
                                .findAll(predicate)
                                .spliterator(),
                        false
                )
                .toList();
    }
}