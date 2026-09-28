package com.halil.dvdrental.bean;

import com.halil.dvdrental.entity.Actor;
import com.halil.dvdrental.entity.Category;
import com.halil.dvdrental.entity.Language;
import com.halil.dvdrental.model.FilmLazyDataModel;
import com.halil.dvdrental.service.ActorService;
import com.halil.dvdrental.service.CategoryService;
import com.halil.dvdrental.service.FilmService;
import com.halil.dvdrental.service.LanguageService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.halil.dvdrental.dto.FilmDTO;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class FilmBean implements Serializable {

    private final FilmService filmService;
    private final LanguageService languageService;
    private final ActorService actorService;
    private final CategoryService categoryService;

    private List<FilmDTO> filmList;
    private List<Language> languageList;
    private List<Actor> actorList;
    private List<Category> categoryList;

    private FilmLazyDataModel lazyFilmModel;

    private FilmDTO selectedFilmDTO = new FilmDTO();

    private String searchKeyword;

    private boolean editMode;


    public void prepareNew() {
        selectedFilmDTO = new FilmDTO();
        editMode = false;
    }

    public void loadFilmDTO(Integer filmId) {

        selectedFilmDTO = filmService.getFilmDTOById(filmId);

        if (selectedFilmDTO != null) {
            log.debug("Loaded film DTO: id={}, title={}, language={}, actorIds={}, categoryIds={}",
                    selectedFilmDTO.getFilmId(),
                    selectedFilmDTO.getTitle(),
                    selectedFilmDTO.getLanguageName(),
                    selectedFilmDTO.getActorIds(),
                    selectedFilmDTO.getCategoryIds());
        }
    }

    public void prepareEdit(FilmDTO film) {
        selectedFilmDTO = filmService.getFilmDTOById(film.getFilmId());
        editMode = true;
    }

    public void saveDTO() {

        if (selectedFilmDTO == null) {
            log.warn("saveDTO called with null selectedFilmDTO");
            return;
        }

        FilmDTO savedDTO = filmService.saveFilmDTO(selectedFilmDTO);

        if (savedDTO == null) {
            log.warn("Film save failed for title={}", selectedFilmDTO.getTitle());
            return;
        }

        log.info("Film saved successfully: id={}, title={}",
            savedDTO.getFilmId(), savedDTO.getTitle());

        lazyFilmModel = new FilmLazyDataModel(filmService);
        lazyFilmModel.setKeyword(searchKeyword);

        selectedFilmDTO = new FilmDTO();
        editMode = false;
    }

    public void delete() {

        boolean deleted = filmService.deleteFilm(selectedFilmDTO.getFilmId());

        if (deleted) {

            filmList = filmService.getAllFilmDTOs();
            selectedFilmDTO = new FilmDTO();

        } else {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Film silinemedi",
                            "Bu film inventory kayıtlarına bağlı olduğu için silinemez."
                    )
            );
        }
    }

    public List<FilmDTO> getFilmList() {
        if (filmList == null) {
            filmList = filmService.getAllFilmDTOs();
        }
        return filmList;
    }

    public List<Language> getLanguageList() {
        if (languageList == null) {
            languageList = languageService.getAllLanguages();
        }
        return languageList;
    }

    public List<Actor> getActorList() {
        if (actorList == null) {
            actorList = actorService.getAllActors();
        }
        return actorList;
    }

    public List<Category> getCategoryList() {
        if (categoryList == null) {
            categoryList = categoryService.getAllCategories();
        }
        return categoryList;
    }

    public FilmLazyDataModel getLazyFilmModel() {
        if (lazyFilmModel == null) {
            lazyFilmModel = new FilmLazyDataModel(filmService);
        }
        return lazyFilmModel;
    }

    public void search() {
        if (lazyFilmModel == null) {
            lazyFilmModel = new FilmLazyDataModel(filmService);
        }
        lazyFilmModel.setKeyword(searchKeyword);
        lazyFilmModel.setRowIndex(0);
    }

    public void clearSearch() {
        searchKeyword = null;

        if (lazyFilmModel == null) {
            lazyFilmModel = new FilmLazyDataModel(filmService);
        }

        lazyFilmModel.setKeyword(null);
        lazyFilmModel.setRowIndex(0);
    }
}