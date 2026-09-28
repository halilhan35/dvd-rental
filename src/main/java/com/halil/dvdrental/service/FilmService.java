package com.halil.dvdrental.service;

import com.halil.dvdrental.entity.Actor;
import com.halil.dvdrental.entity.Film;
import com.halil.dvdrental.entity.QFilm;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.halil.dvdrental.entity.Category;
import com.halil.dvdrental.repository.ActorRepository;
import com.halil.dvdrental.repository.CategoryRepository;
import com.halil.dvdrental.repository.FilmRepository;
import com.halil.dvdrental.security.SecurityUtils;

import java.util.*;
import java.util.stream.StreamSupport;
import com.halil.dvdrental.dto.FilmDTO;
import com.halil.dvdrental.mapper.FilmMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final JPAQueryFactory queryFactory;
    private final ActorRepository actorRepository;
    private final CategoryRepository categoryRepository;
    private final LanguageService languageService;
    private static final Logger filmAuditLog = LoggerFactory.getLogger("FILM_AUDIT");
    private static final DateTimeFormatter AUDIT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


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

    @Cacheable(value = "filmById", key = "#id")
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

    @Cacheable("allFilms")
    public List<FilmDTO> getAllFilmDTOs() {
        log.debug("Fetching all films from database (cache miss)");
        return getAllFilms()
                .stream()
                .map(FilmMapper::toListDTO)
                .toList();
    }

    @PreAuthorize("hasRole('STAFF')")
    @CacheEvict(value = "filmById", key = "#dto.filmId", condition = "#dto.filmId != null")
    public FilmDTO saveFilmDTO(FilmDTO dto) {

        Film film;
        boolean isNew = dto.getFilmId() == null;
        List<String> changes = new ArrayList<>();

        if (isNew) {

            film = FilmMapper.toEntity(dto);

            if (dto.getLanguageId() != null) {
                languageService.getLanguageById(dto.getLanguageId()).ifPresent(film::setLanguage);
            }

            List<Actor> actors = actorRepository.findAllById(dto.getActorIds());
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());

            film.setActors(new LinkedHashSet<>(actors));
            film.setCategories(new LinkedHashSet<>(categories));

            film = filmRepository.save(film);

        } else {

            film = getFilmForEdit(dto.getFilmId());

            if (film == null) {
                logFilmAudit("Film Güncelleme", dto.getFilmId(), dto.getTitle(), List.of(), false);
                return null;
            }

            if (!Objects.equals(film.getTitle(), dto.getTitle())) {
                changes.add("Film adı " + film.getTitle() + " → " + dto.getTitle());
            }
            if (!Objects.equals(film.getReleaseYear(), dto.getReleaseYear())) {
                changes.add("Yayın yılı " + film.getReleaseYear() + " → " + dto.getReleaseYear());
            }
            if (!Objects.equals(film.getRentalRate(), dto.getRentalRate())) {
                changes.add("Kiralama ücreti " + film.getRentalRate() + " → " + dto.getRentalRate());
            }
            if (!Objects.equals(film.getDescription(), dto.getDescription())) {
                changes.add("Açıklama değişti");
            }

            film.setTitle(dto.getTitle());
            film.setDescription(dto.getDescription());
            film.setReleaseYear(dto.getReleaseYear());
            film.setRentalRate(dto.getRentalRate());
            film.setLength(dto.getLength());

            if (dto.getLanguageId() != null) {
                languageService.getLanguageById(dto.getLanguageId()).ifPresent(film::setLanguage);
            } else {
                film.setLanguage(null);
            }

            List<Actor> actors = actorRepository.findAllById(dto.getActorIds());
            film.getActors().clear();
            film.getActors().addAll(actors);

            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            film.getCategories().clear();
            film.getCategories().addAll(categories);
        }

        logFilmAudit(isNew ? "Film Ekleme" : "Film Güncelleme", film.getFilmId(), film.getTitle(), changes, true);

        return FilmMapper.toDTO(film);
    }

    private void logFilmAudit(String action, Integer filmId, String filmTitle, List<String> changes, boolean success) {

        String changesText = changes.isEmpty() ? "-" : String.join(" | ", changes);

        String header = String.format("%-19s | %-15s | %-16s | %-8s | %-25s | %-45s | %-10s",
                "Tarih", "Kullanıcı", "İşlem", "Film ID", "Film Adı", "Değişiklikler", "Sonuç");

        String row = String.format("%-19s | %-15s | %-16s | %-8s | %-25s | %-45s | %-10s",
                LocalDateTime.now().format(AUDIT_FORMAT),
                SecurityUtils.getCurrentUserFullName(),
                action,
                filmId,
                filmTitle,
                changesText,
                success ? "Başarılı" : "Başarısız");

        String separator = "-".repeat(header.length());

        filmAuditLog.info("\n" + header + "\n" + separator + "\n" + row + "\n" + separator);
    }

    @PreAuthorize("hasRole('STAFF')")
    @CacheEvict(value = "filmById", key = "#id")
    public boolean deleteFilm(Integer id) {

        Optional<Film> filmOpt = filmRepository.findById(id);
        String filmTitle = filmOpt.map(Film::getTitle).orElse("Bilinmiyor");

        long inventoryCount = filmRepository.countInventoryByFilmId(id);

        if (inventoryCount > 0) {
            logFilmAudit("Film Silme", id, filmTitle, List.of(), false);
            return false;
        }

        filmRepository.deleteById(id);
        logFilmAudit("Film Silme", id, filmTitle, List.of(), true);
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