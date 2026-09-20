package com.halil.dvdrental.service;

import com.halil.dvdrental.entity.Language;
import com.halil.dvdrental.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public Optional<Language> getLanguageById(Integer id) {
        return languageRepository.findById(id);
    }

}