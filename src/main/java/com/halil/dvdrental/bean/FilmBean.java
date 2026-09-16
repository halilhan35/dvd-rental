package com.halil.dvdrental.bean;

import com.halil.dvdrental.entity.Film;
import com.halil.dvdrental.entity.Language;
import com.halil.dvdrental.service.FilmService;
import com.halil.dvdrental.service.LanguageService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class FilmBean implements Serializable {

    private final FilmService filmService;
    private final LanguageService languageService;

    private List<Film> filmList;
    private List<Language> languageList;

    private Film selectedFilm = new Film();
    private Integer selectedLanguageId;
    private String searchKeyword;

    private boolean editMode;

    @Autowired
    public FilmBean(FilmService filmService,
                    LanguageService languageService) {

        this.filmService = filmService;
        this.languageService = languageService;
    }

    public void prepareNew() {
        selectedFilm = new Film();
        selectedLanguageId = null;
        editMode = false;
    }

    public void prepareEdit(Film film) {
        selectedFilm = film;
        editMode = true;

        if (film.getLanguage() != null) {
            selectedLanguageId = film.getLanguage().getLanguageId();
        } else {
            selectedLanguageId = null;
        }
    }

    public void save() {

        if (selectedLanguageId != null) {
            languageService.getLanguageById(selectedLanguageId)
                    .ifPresent(selectedFilm::setLanguage);
        }

        filmService.saveFilm(selectedFilm);

        filmList = filmService.getAllFilms();

        selectedFilm = new Film();
        selectedLanguageId = null;
    }

    public void delete() {
        filmService.deleteFilm(selectedFilm.getFilmId());

        filmList = filmService.getAllFilms();

        selectedFilm = new Film();
        selectedLanguageId = null;
    }

    public List<Film> getFilmList() {
        if (filmList == null) {
            filmList = filmService.getAllFilms();
        }

        return filmList;
    }

    public List<Language> getLanguageList() {
        if (languageList == null) {
            languageList = languageService.getAllLanguages();
        }

        return languageList;
    }

    public void search() {

        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            filmList = filmService.getAllFilms();
            return;
        }

        filmList = filmService.searchFilms(searchKeyword);
    }

    public void clearSearch() {
        searchKeyword = null;
        filmList = filmService.getAllFilms();
    }

    public Film getSelectedFilm() {
        return selectedFilm;
    }

    public void setSelectedFilm(Film selectedFilm) {
        this.selectedFilm = selectedFilm;
    }

    public Integer getSelectedLanguageId() {
        return selectedLanguageId;
    }

    public void setSelectedLanguageId(Integer selectedLanguageId) {
        this.selectedLanguageId = selectedLanguageId;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}