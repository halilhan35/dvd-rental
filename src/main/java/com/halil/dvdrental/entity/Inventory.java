package com.halil.dvdrental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    @Id
    @Column(name = "inventory_id")
    private Integer inventoryId;

    @Column(name = "film_id")
    private Integer filmId;

    @Column(name = "store_id")
    private Integer storeId;
}