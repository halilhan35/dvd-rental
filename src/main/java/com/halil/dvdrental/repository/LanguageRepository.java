package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
}