package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    java.util.List<Inventory> findByFilmId(Integer filmId);
}