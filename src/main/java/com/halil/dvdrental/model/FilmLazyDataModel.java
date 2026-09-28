package com.halil.dvdrental.model;

import com.halil.dvdrental.service.FilmService;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import com.halil.dvdrental.dto.FilmDTO;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class FilmLazyDataModel extends LazyDataModel<FilmDTO> {

    private final FilmService filmService;
    private String keyword;

    public FilmLazyDataModel(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public List<FilmDTO> load(int first,
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

        List<FilmDTO> result = filmService.getFilmDTOs(
                first,
                pageSize,
                sortField,
                ascending,
                keyword
        );

        return result;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) filmService.countFilms(keyword);
    }
}