package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}