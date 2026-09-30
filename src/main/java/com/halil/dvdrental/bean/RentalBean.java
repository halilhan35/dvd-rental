package com.halil.dvdrental.bean;

import com.halil.dvdrental.dto.RentalDTO;
import com.halil.dvdrental.service.RentalService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@RequiredArgsConstructor
@Slf4j
public class RentalBean implements Serializable {

    private final RentalService rentalService;

    @Inject
    private LoggedInUserBean loggedInUserBean;

    public void rent(Integer filmId) {

        Integer customerId = loggedInUserBean.getUserId();

        boolean success = rentalService.rentFilm(filmId, customerId);

        FacesContext.getCurrentInstance().addMessage(
                null,
                success
                        ? new FacesMessage(FacesMessage.SEVERITY_INFO, "Başarılı", "Film kiralandı!")
                        : new FacesMessage(FacesMessage.SEVERITY_WARN, "Müsait Değil", "Bu filmin şu an müsait kopyası yok.")
        );
    }

    private List<RentalDTO> myRentals;

    public List<RentalDTO> getMyRentals() {
        if (myRentals == null) {
            Integer customerId = loggedInUserBean.getUserId();
            myRentals = rentalService.getRentalsByCustomer(customerId);
        }
        return myRentals;
    }


    private List<RentalDTO> activeRentals;

    public List<RentalDTO> getActiveRentals() {
        if (activeRentals == null) {
            activeRentals = rentalService.getActiveRentals();
        }
        return activeRentals;
    }


    public void returnFilm(Integer rentalId) {

        log.info("RentalBean.returnFilm çağrıldı: rentalId={}", rentalId);

        boolean success;

        try {
            success = rentalService.returnFilm(rentalId);
        } catch (Exception e) {
            log.error("İade sırasında hata: rentalId={}", rentalId, e);
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hata",
                            "İade yapılamadı: " + e.getClass().getSimpleName())
            );
            return;
        }

        FacesContext.getCurrentInstance().addMessage(
                null,
                success
                        ? new FacesMessage(FacesMessage.SEVERITY_INFO, "Başarılı", "Film iade alındı!")
                        : new FacesMessage(FacesMessage.SEVERITY_WARN, "Hata", "İade işlemi başarısız oldu.")
        );

        activeRentals = null;
    }

}