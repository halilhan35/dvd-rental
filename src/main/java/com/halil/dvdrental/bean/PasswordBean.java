package com.halil.dvdrental.bean;

import com.halil.dvdrental.service.PasswordChangeResult;
import com.halil.dvdrental.service.PasswordService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Named
@ViewScoped
@RequiredArgsConstructor
@Slf4j
public class PasswordBean implements Serializable {

    private final PasswordService passwordService;

    @Getter @Setter private String currentPassword;
    @Getter @Setter private String newPassword;
    @Getter @Setter private String confirmPassword;

    public void changePassword() {
        try {
            if (newPassword == null || !newPassword.equals(confirmPassword)) {
                addMessage(FacesMessage.SEVERITY_WARN, "Şifreler eşleşmiyor",
                        "Yeni şifre ve tekrarı aynı olmalıdır.");
                return;
            }

            PasswordChangeResult result =
                    passwordService.changeOwnPassword(currentPassword, newPassword);
            showResult(result);

        } catch (Exception e) {
            log.error("Şifre değiştirme sırasında hata", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Hata",
                    "Şifre değiştirilemedi: " + e.getClass().getSimpleName());
        } finally {
            currentPassword = null;
            newPassword = null;
            confirmPassword = null;
        }
    }

    private void showResult(PasswordChangeResult result) {
        switch (result) {
            case SUCCESS -> addMessage(FacesMessage.SEVERITY_INFO, "Başarılı",
                    "Şifreniz değiştirildi.");
            case WRONG_CURRENT_PASSWORD -> addMessage(FacesMessage.SEVERITY_WARN, "Hata",
                    "Mevcut şifre yanlış.");
            case SAME_AS_CURRENT -> addMessage(FacesMessage.SEVERITY_WARN, "Hata",
                    "Yeni şifre mevcut şifreyle aynı olamaz.");
            case TOO_SHORT -> addMessage(FacesMessage.SEVERITY_WARN, "Hata",
                    "Yeni şifre en az 4 karakter olmalıdır.");
            case TOO_LONG -> addMessage(FacesMessage.SEVERITY_WARN, "Hata",
                    "Yeni şifre çok uzun (en fazla 72 bayt).");
            case USER_NOT_FOUND -> addMessage(FacesMessage.SEVERITY_ERROR, "Hata",
                    "Kullanıcı bulunamadı, lütfen tekrar giriş yapın.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, summary, detail));
    }
}