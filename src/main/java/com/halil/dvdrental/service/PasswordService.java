package com.halil.dvdrental.service;

import com.halil.dvdrental.audit.AuditLog;
import com.halil.dvdrental.entity.Customer;
import com.halil.dvdrental.entity.Staff;
import com.halil.dvdrental.repository.CustomerRepository;
import com.halil.dvdrental.repository.StaffRepository;
import com.halil.dvdrental.security.AppUserDetails;
import com.halil.dvdrental.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordService {

    private static final int MIN_LENGTH = 4;
    private static final int MAX_BYTES = 72;

    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("isAuthenticated()")
    @AuditLog("Şifre Değişikliği")
    public PasswordChangeResult changeOwnPassword(String currentPassword, String newPassword) {

        Optional<AppUserDetails> current = SecurityUtils.getCurrentUser();
        if (current.isEmpty()) {
            return PasswordChangeResult.USER_NOT_FOUND;
        }
        AppUserDetails user = current.get();

        if ("STAFF".equals(user.getRole())) {

            Staff staff = staffRepository.findById(user.getId()).orElse(null);
            if (staff == null) {
                return PasswordChangeResult.USER_NOT_FOUND;
            }

            PasswordChangeResult check = validate(currentPassword, newPassword, staff.getPassword());
            if (check != PasswordChangeResult.SUCCESS) {
                return check;
            }

            staff.setPassword(passwordEncoder.encode(newPassword));
            staffRepository.save(staff);

        } else {

            Customer customer = customerRepository.findById(user.getId()).orElse(null);
            if (customer == null) {
                return PasswordChangeResult.USER_NOT_FOUND;
            }

            PasswordChangeResult check = validate(currentPassword, newPassword, customer.getPassword());
            if (check != PasswordChangeResult.SUCCESS) {
                return check;
            }

            customer.setPassword(passwordEncoder.encode(newPassword));
            customerRepository.save(customer);
        }

        return PasswordChangeResult.SUCCESS;
    }

    private PasswordChangeResult validate(String currentPassword, String newPassword, String storedHash) {

        if (currentPassword == null || storedHash == null
                || !passwordEncoder.matches(currentPassword, storedHash)) {
            return PasswordChangeResult.WRONG_CURRENT_PASSWORD;
        }
        if (newPassword == null || newPassword.length() < MIN_LENGTH) {
            return PasswordChangeResult.TOO_SHORT;
        }
        if (newPassword.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
            return PasswordChangeResult.TOO_LONG;
        }
        if (passwordEncoder.matches(newPassword, storedHash)) {
            return PasswordChangeResult.SAME_AS_CURRENT;
        }
        return PasswordChangeResult.SUCCESS;
    }
}