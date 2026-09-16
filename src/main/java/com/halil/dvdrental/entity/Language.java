package com.halil.dvdrental.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "language")
public class Language {

    @Id
    @Column(name = "language_id")
    private Integer languageId;

    @Column(name = "name")
    private String name;

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}