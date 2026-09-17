package com.halil.dvdrental.bean;

import com.halil.dvdrental.entity.Actor;
import com.halil.dvdrental.entity.Category;
import com.halil.dvdrental.entity.Film;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class FilmBean implements Serializable {

    private final FilmService filmService;
    private final LanguageService languageService;
    private final ActorService actorService;
    private final CategoryService categoryService;

    private List<Film> filmList;
    private List<Language> languageList;
    private List<Actor> actorList;
    private List<Category> categoryList;

    private FilmLazyDataModel lazyFilmModel;

    private Film selectedFilm = new Film();

    private Integer selectedLanguageId;

    private Set<Integer> selectedActorIds = new LinkedHashSet<>();
    private Set<Integer> selectedCategoryIds = new LinkedHashSet<>();

    private String searchKeyword;

    private boolean editMode;

    @Autowired
    public FilmBean(FilmService filmService,
                    LanguageService languageService,
                    ActorService actorService,
                    CategoryService categoryService) {

        this.filmService = filmService;
        this.languageService = languageService;
        this.actorService = actorService;
        this.categoryService = categoryService;
    }

    // =========================
    // Yeni film
    // =========================

    public void prepareNew() {

        selectedFilm = new Film();

        selectedLanguageId = null;

        selectedActorIds = new LinkedHashSet<>();
        selectedCategoryIds = new LinkedHashSet<>();

        editMode = false;
    }

    // =========================
    // Film düzenleme
    // =========================

    public void prepareEdit(Film film) {

        filmService.getFilmByIdWithDetails(film.getFilmId())
                .ifPresent(f -> {

                    selectedFilm = f;

                    // Dil
                    selectedLanguageId =
                            (f.getLanguage() != null)
                                    ? f.getLanguage().getLanguageId()
                                    : null;

                    // Actor ID'lerini al
                    selectedActorIds = f.getActors()
                            .stream()
                            .map(Actor::getActorId)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    // Category ID'lerini al
                    selectedCategoryIds = f.getCategories()
                            .stream()
                            .map(Category::getCategoryId)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    editMode = true;
                });
    }

    // =========================
    // Kaydet
    // =========================

    public void save() {

        // Language
        if (selectedLanguageId != null) {

            languageService
                    .getLanguageById(selectedLanguageId)
                    .ifPresent(selectedFilm::setLanguage);
        }

        // Actor ve Category ilişkilerini
        // ID üzerinden Service'e gönderiyoruz.
        filmService.saveFilm(
                selectedFilm,
                selectedActorIds,
                selectedCategoryIds
        );

        // Listeyi yenile
        filmList = filmService.getAllFilms();

        // Formu temizle
        selectedFilm = new Film();

        selectedLanguageId = null;

        selectedActorIds = new LinkedHashSet<>();
        selectedCategoryIds = new LinkedHashSet<>();

        editMode = false;
    }

    // =========================
    // Sil
    // =========================

    public void delete() {
        boolean deleted = filmService.deleteFilm(selectedFilm.getFilmId());

        if (deleted) {
            filmList = filmService.getAllFilms();
            selectedFilm = new Film();
            selectedLanguageId = null;
            selectedActorIds = new LinkedHashSet<>();
            selectedCategoryIds = new LinkedHashSet<>();
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

    // =========================
    // Film listesi
    // =========================

    public List<Film> getFilmList() {

        if (filmList == null) {
            filmList = filmService.getAllFilms();
        }

        return filmList;
    }

    // =========================
    // Language listesi
    // =========================

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

    // =========================
    // Category listesi
    // =========================

    public List<Category> getCategoryList() {

        if (categoryList == null) {
            categoryList = categoryService.getAllCategories();
        }

        return categoryList;
    }

    // =========================
    // Lazy DataModel
    // =========================

    public FilmLazyDataModel getLazyFilmModel() {

        if (lazyFilmModel == null) {
            lazyFilmModel = new FilmLazyDataModel(filmService);
        }

        return lazyFilmModel;
    }

    // =========================
    // Arama
    // =========================

    public void search() {

        if (lazyFilmModel == null) {
            lazyFilmModel = new FilmLazyDataModel(filmService);
        }

        lazyFilmModel.setKeyword(searchKeyword);
        lazyFilmModel.setRowIndex(0);
    }

    // =========================
    // Aramayı temizle
    // =========================

    public void clearSearch() {

        searchKeyword = null;

        if (lazyFilmModel == null) {
            lazyFilmModel = new FilmLazyDataModel(filmService);
        }

        lazyFilmModel.setKeyword(null);
        lazyFilmModel.setRowIndex(0);
    }

    // =========================
    // Getter / Setter
    // =========================

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

    public Set<Integer> getSelectedActorIds() {
        return selectedActorIds;
    }

    public void setSelectedActorIds(Set<Integer> selectedActorIds) {
        this.selectedActorIds = selectedActorIds;
    }

    public Set<Integer> getSelectedCategoryIds() {
        return selectedCategoryIds;
    }

    public void setSelectedCategoryIds(Set<Integer> selectedCategoryIds) {
        this.selectedCategoryIds = selectedCategoryIds;
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