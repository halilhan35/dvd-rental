package com.halil.dvdrental.model;

import com.halil.dvdrental.entity.Film;
import com.halil.dvdrental.service.FilmService;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.util.List;
import java.util.Map;

public class FilmLazyDataModel extends LazyDataModel<Film> {

    private final FilmService filmService;
    private String keyword;

    public FilmLazyDataModel(FilmService filmService) {
        this.filmService = filmService;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public List<Film> load(int first,
                           int pageSize,
                           Map<String, SortMeta> sortBy,
                           Map<String, FilterMeta> filterBy) {

        String sortField = "filmId";
        boolean ascending = true;

        if (sortBy != null && !sortBy.isEmpty()) {

            SortMeta sortMeta = sortBy.values().iterator().next();

            if (sortMeta.getField() != null) {
                sortField = sortMeta.getField();
            }

            ascending = sortMeta.getOrder() == null
                    || sortMeta.getOrder().isAscending();
        }

        setRowCount((int) filmService.countFilms(keyword));

        return filmService.getFilms(
                first,
                pageSize,
                sortField,
                ascending,
                keyword
        );
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) filmService.countFilms(keyword);
    }
}