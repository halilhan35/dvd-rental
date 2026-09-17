package com.halil.dvdrental.service;

import com.halil.dvdrental.entity.Film;
import com.halil.dvdrental.entity.QFilm;
import com.halil.dvdrental.repository.FilmRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halil.dvdrental.entity.Actor;
import com.halil.dvdrental.entity.Category;
import com.halil.dvdrental.repository.ActorRepository;
import com.halil.dvdrental.repository.CategoryRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class FilmService {

    private final FilmRepository filmRepository;
    private final JPAQueryFactory queryFactory;
    private final ActorRepository actorRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository,
                       JPAQueryFactory queryFactory,
                       ActorRepository actorRepository,
                       CategoryRepository categoryRepository) {

        this.filmRepository = filmRepository;
        this.queryFactory = queryFactory;
        this.actorRepository = actorRepository;
        this.categoryRepository = categoryRepository;
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
     * Düzenleme ekranı için kullanılır.
     *
     * Burada Hibernate collection'larını normal Set'e
     * çeviriyoruz.
     *
     * Çünkü bu nesne JSF tarafına gönderilecek ve Hibernate
     * session'ı kapandıktan sonra JSF actors/categories
     * alanlarını okuyacak.
     */
    @Transactional(readOnly = true)
    public Optional<Film> getFilmByIdWithDetails(Integer id) {

        QFilm film = QFilm.film;

        Film result = queryFactory
                .selectFrom(film)
                .leftJoin(film.language).fetchJoin()
                .leftJoin(film.actors).fetchJoin()
                .leftJoin(film.categories).fetchJoin()
                .where(film.filmId.eq(id))
                .distinct()
                .fetchOne();

        if (result == null) {
            return Optional.empty();
        }

        /*
         * Collection'ların transaction açıkken initialize
         * edilmesini garanti ediyoruz.
         */
        result.getActors().size();
        result.getCategories().size();

        /*
         * JSF tarafına Hibernate PersistentSet göndermiyoruz.
         */
        result.setActors(
                new HashSet<>(result.getActors())
        );

        result.setCategories(
                new HashSet<>(result.getCategories())
        );

        return Optional.of(result);
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
        return filmRepository.findAll();
    }

    public Optional<Film> getFilmById(Integer id) {
        return filmRepository.findById(id);
    }

    /*
     * Film kaydetme / güncelleme
     */

    @Transactional
    public Film saveFilm(Film film,
                         Set<Integer> actorIds,
                         Set<Integer> categoryIds) {

        /*
         * YENİ FİLM
         */
        if (film.getFilmId() == null) {

            // Önce filmi kaydet
            Film savedFilm = filmRepository.save(film);

            // Seçilen Actor'ları veritabanından managed olarak getir
            List<Actor> actors =
                    actorRepository.findAllById(actorIds);

            // Seçilen Category'leri veritabanından managed olarak getir
            List<Category> categories =
                    categoryRepository.findAllById(categoryIds);

            // İlişkileri ekle
            savedFilm.getActors().clear();
            savedFilm.getActors().addAll(actors);

            savedFilm.getCategories().clear();
            savedFilm.getCategories().addAll(categories);

            return savedFilm;
        }

        /*
         * MEVCUT FİLMİ GETİR
         */
        Film existingFilm =
                getFilmForEdit(film.getFilmId());

        if (existingFilm == null) {
            return null;
        }

        /*
         * Normal alanlar
         */
        existingFilm.setTitle(
                film.getTitle()
        );

        existingFilm.setDescription(
                film.getDescription()
        );

        existingFilm.setReleaseYear(
                film.getReleaseYear()
        );

        existingFilm.setLanguage(
                film.getLanguage()
        );

        existingFilm.setRentalRate(
                film.getRentalRate()
        );

        existingFilm.setLength(
                film.getLength()
        );

        /*
         * ACTOR
         */

        List<Actor> actors =
                actorRepository.findAllById(actorIds);

        existingFilm.getActors().clear();

        existingFilm.getActors().addAll(actors);

        /*
         * CATEGORY
         */

        List<Category> categories =
                categoryRepository.findAllById(categoryIds);

        existingFilm.getCategories().clear();

        existingFilm.getCategories().addAll(categories);

        /*
         * existingFilm Hibernate tarafından yönetiliyor.
         * Transaction sonunda değişiklikler otomatik olarak
         * veritabanına yazılır.
         */
        return existingFilm;
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