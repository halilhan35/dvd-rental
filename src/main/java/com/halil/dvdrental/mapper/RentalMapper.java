package com.halil.dvdrental.mapper;

import com.halil.dvdrental.dto.RentalDTO;
import com.halil.dvdrental.entity.Rental;

public class RentalMapper {

    public static RentalDTO toDTO(Rental rental, String filmTitle, String customerName) {

        RentalDTO dto = new RentalDTO();

        dto.setRentalId(rental.getRentalId());
        dto.setFilmTitle(filmTitle);
        dto.setCustomerName(customerName);
        dto.setRentalDate(rental.getRentalDate());
        dto.setReturnDate(rental.getReturnDate());
        dto.setInventoryId(rental.getInventoryId());
        dto.setCustomerId(rental.getCustomerId());

        return dto;
    }
}