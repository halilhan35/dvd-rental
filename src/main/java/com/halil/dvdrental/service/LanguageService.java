package com.halil.dvdrental.service;

import com.halil.dvdrental.entity.Language;
import com.halil.dvdrental.repository.LanguageRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.List;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public Optional<Language> getLanguageById(Integer id) {
        return languageRepository.findById(id);
    }

}