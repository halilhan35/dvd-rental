package com.halil.dvdrental.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {

    public static Optional<AppUserDetails> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails details) {
            return Optional.of(details);
        }
        return Optional.empty();
    }

    public static String getCurrentUserFullName() {
        return getCurrentUser()
                .map(AppUserDetails::getFullName)
                .orElse("Bilinmiyor");
    }
}