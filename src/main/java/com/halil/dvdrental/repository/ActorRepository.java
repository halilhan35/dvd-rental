package com.halil.dvdrental.repository;

import com.halil.dvdrental.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Integer> {
}