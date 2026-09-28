package com.halil.dvdrental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "store_id", nullable = false)
    private Short storeId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "address_id", nullable = false)
    private Short addressId;

    @Column(name = "activebool", nullable = false)
    private Boolean activebool;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @Column(name = "last_update")
    private LocalDate lastUpdate;

    @Column(name = "active")
    private Integer active;

    @Column(name = "password")
    private String password;
}