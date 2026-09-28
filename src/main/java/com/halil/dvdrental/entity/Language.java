package com.halil.dvdrental.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "language")
public class Language implements Serializable {

    @Id
    @Column(name = "language_id")
    private Integer languageId;

    @Column(name = "name")
    private String name;
}