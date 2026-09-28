package com.halil.dvdrental.service;

import com.halil.dvdrental.dto.RentalDTO;
import com.halil.dvdrental.entity.Film;
import com.halil.dvdrental.entity.Inventory;
import com.halil.dvdrental.entity.Rental;
import com.halil.dvdrental.mapper.RentalMapper;
import com.halil.dvdrental.repository.CustomerRepository;
import com.halil.dvdrental.repository.FilmRepository;
import com.halil.dvdrental.repository.InventoryRepository;
import com.halil.dvdrental.repository.RentalRepository;
import com.halil.dvdrental.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RentalService {

    private static final Integer SYSTEM_STAFF_ID = 1;

    private final RentalRepository rentalRepository;
    private final InventoryRepository inventoryRepository;
    private final FilmRepository filmRepository;
    private final CustomerRepository customerRepository;
    private static final Logger rentalAuditLog = LoggerFactory.getLogger("RENTAL_AUDIT");
    private static final DateTimeFormatter AUDIT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean isFilmAvailable(Integer filmId) {
        return findAvailableInventory(filmId).isPresent();
    }

    private Optional<Inventory> findAvailableInventory(Integer filmId) {

        List<Inventory> copies = inventoryRepository.findByFilmId(filmId);

        return copies.stream()
                .filter(copy -> rentalRepository
                        .findByInventoryIdAndReturnDateIsNull(copy.getInventoryId())
                        .isEmpty())
                .findFirst();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public boolean rentFilm(Integer filmId, Integer customerId) {

        Optional<Inventory> availableCopy = findAvailableInventory(filmId);

        String filmTitle = filmRepository.findById(filmId).map(Film::getTitle).orElse("Bilinmiyor");

        if (availableCopy.isEmpty()) {
            log.warn("Film müsait değil: filmId={}", filmId);
            logRentalAudit("Kiralama", null, filmTitle, "Müsait Değil", null, null, false);
            return false;
        }

        Rental rental = new Rental();
        rental.setInventoryId(availableCopy.get().getInventoryId());
        rental.setCustomerId(customerId);
        rental.setStaffId(SYSTEM_STAFF_ID);
        rental.setRentalDate(LocalDateTime.now());
        rental.setReturnDate(null);

        rentalRepository.save(rental);

        log.info("Film kiralandı: filmId={}, customerId={}", filmId, customerId);
        logRentalAudit("Kiralama", rental.getRentalId(), filmTitle, "Kiralandı", rental.getRentalDate(), null, true);

        return true;
    }

    @PreAuthorize("hasRole('STAFF')")
    public List<RentalDTO> getActiveRentals() {
        return rentalRepository.findByReturnDateIsNullOrderByRentalDateDesc()
                .stream()
                .map(this::enrichToDTO)
                .toList();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public List<RentalDTO> getRentalsByCustomer(Integer customerId) {
        return rentalRepository.findByCustomerIdOrderByRentalDateDesc(customerId)
                .stream()
                .map(this::enrichToDTO)
                .toList();
    }

    private void logRentalAudit(String action, Integer rentalId, String filmTitle,
                                String status, LocalDateTime rentalDate,
                                LocalDateTime returnDate, boolean success) {

        String header = String.format("%-19s | %-15s | %-8s | %-25s | %-14s | %-19s | %-19s | %-10s",
                "Tarih", "Kullanıcı", "Film ID", "Film Adı", "Durum", "Kiralama Tarihi", "İade Tarihi", "Sonuç");

        String row = String.format("%-19s | %-15s | %-8s | %-25s | %-14s | %-19s | %-19s | %-10s",
                LocalDateTime.now().format(AUDIT_FORMAT),
                SecurityUtils.getCurrentUserFullName(),
                rentalId,
                filmTitle,
                status,
                rentalDate != null ? rentalDate.format(AUDIT_FORMAT) : "-",
                returnDate != null ? returnDate.format(AUDIT_FORMAT) : "-",
                success ? "Başarılı" : "Başarısız");

        String separator = "-".repeat(header.length());

        rentalAuditLog.info("\n" + header + "\n" + separator + "\n" + row + "\n" + separator);
    }

    private RentalDTO enrichToDTO(Rental rental) {

        String filmTitle = inventoryRepository.findById(rental.getInventoryId())
                .flatMap(inv -> filmRepository.findById(inv.getFilmId()))
                .map(Film::getTitle)
                .orElse("Bilinmiyor");

        String customerName = customerRepository.findById(rental.getCustomerId())
                .map(c -> c.getFirstName() + " " + c.getLastName())
                .orElse("Bilinmiyor");

        return RentalMapper.toDTO(rental, filmTitle, customerName);
    }

    @PreAuthorize("hasRole('STAFF')")
    public boolean returnFilm(Integer rentalId) {

        Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);

        if (rentalOpt.isEmpty()) {
            logRentalAudit("İade", rentalId, "Bilinmiyor", "Bulunamadı", null, null, false);
            return false;
        }

        Rental rental = rentalOpt.get();
        String filmTitle = inventoryRepository.findById(rental.getInventoryId())
                .flatMap(inv -> filmRepository.findById(inv.getFilmId()))
                .map(Film::getTitle)
                .orElse("Bilinmiyor");

        if (rental.getReturnDate() != null) {
            log.warn("Bu kiralama zaten iade edilmiş: rentalId={}", rentalId);
            logRentalAudit("İade", rentalId, filmTitle, "Zaten İade Edilmiş", rental.getRentalDate(), rental.getReturnDate(), false);
            return false;
        }

        rental.setReturnDate(LocalDateTime.now());
        rentalRepository.save(rental);

        log.info("Film iade alındı: rentalId={}", rentalId);
        logRentalAudit("İade", rentalId, filmTitle, "İade Edildi", rental.getRentalDate(), rental.getReturnDate(), true);

        return true;
    }
}