package com.halil.dvdrental.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class RentalDTO implements Serializable {

    private Integer rentalId;
    private String filmTitle;
    private String customerName;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private Integer inventoryId;
    private Integer customerId;

    public String getRentalDateFormatted() {
        return rentalDate != null
                ? rentalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                : "-";
    }

    public String getReturnDateFormatted() {
        return returnDate != null
                ? returnDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                : "-";
    }

}

