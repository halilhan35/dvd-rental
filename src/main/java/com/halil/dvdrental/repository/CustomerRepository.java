package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query(
            value = "SELECT COUNT(*) FROM rental WHERE customer_id = :customerId",
            nativeQuery = true
    )
    long countRentalsByCustomerId(@Param("customerId") Integer customerId);
}