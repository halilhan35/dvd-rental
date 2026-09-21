package com.halil.dvdrental.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO {

    private Integer customerId;

    private Short storeId = 1;

    private String firstName;

    private String lastName;

    private String email;

    private Short addressId = 1;

    private Boolean activebool = true;
}