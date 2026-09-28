package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);

    @Query(
            value = "SELECT COUNT(*) FROM rental WHERE customer_id = :customerId",
            nativeQuery = true
    )
    long countRentalsByCustomerId(@Param("customerId") Integer customerId);
}