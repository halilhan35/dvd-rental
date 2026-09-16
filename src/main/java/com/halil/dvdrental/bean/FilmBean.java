package com.halil.dvdrental.bean;

import com.halil.dvdrental.entity.Film;
import com.halil.dvdrental.service.FilmService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class FilmBean implements Serializable {

    private final FilmService filmService;

    private List<Film> filmList;

    private Film selectedFilm;

    private boolean editMode;

    @Autowired
    public FilmBean(FilmService filmService) {
        this.filmService = filmService;
    }

    public void prepareNew() {
        selectedFilm = new Film();
        editMode = false;
    }

    public void prepareEdit(Film film) {
        selectedFilm = film;
        editMode = true;
    }

    public void save() {

        filmService.saveFilm(selectedFilm);

        filmList = filmService.getAllFilms();

        selectedFilm = new Film();
    }

    public void delete() {

        filmService.deleteFilm(selectedFilm.getFilmId());

        filmList = filmService.getAllFilms();

        selectedFilm = new Film();
    }

    public List<Film> getFilmList() {

        if (filmList == null) {
            filmList = filmService.getAllFilms();
        }

        return filmList;
    }

    public Film getSelectedFilm() {
        return selectedFilm;
    }

    public void setSelectedFilm(Film selectedFilm) {
        this.selectedFilm = selectedFilm;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}