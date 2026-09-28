package com.halil.dvdrental.bean;

import com.halil.dvdrental.security.AppUserDetails;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

@Named
@ViewScoped
public class LoggedInUserBean implements Serializable {

    public boolean isStaff() {
        return hasRole("ROLE_STAFF");
    }

    public boolean isCustomer() {
        return hasRole("ROLE_CUSTOMER");
    }

    public String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    public Integer getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails details)) {
            return null;
        }
        return details.getId();
    }

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals(role));
    }
}