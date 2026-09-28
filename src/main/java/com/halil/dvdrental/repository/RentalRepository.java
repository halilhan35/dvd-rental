package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

    List<Rental> findByCustomerIdOrderByRentalDateDesc(Integer customerId);

    List<Rental> findByReturnDateIsNull();

    List<Rental> findByReturnDateIsNullOrderByRentalDateDesc();

    Optional<Rental> findByInventoryIdAndReturnDateIsNull(Integer inventoryId);
}