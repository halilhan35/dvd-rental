package com.halil.dvdrental.service;

import com.halil.dvdrental.entity.Actor;
import com.halil.dvdrental.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }
}