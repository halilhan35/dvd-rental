package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Integer> {

    Optional<Staff> findByUsername(String username);
}