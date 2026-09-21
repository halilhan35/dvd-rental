package com.halil.dvdrental.mapper;

import com.halil.dvdrental.dto.CustomerDTO;
import com.halil.dvdrental.entity.Customer;

import java.time.LocalDate;

public class CustomerMapper {

    public static CustomerDTO toDTO(Customer customer) {

        CustomerDTO dto = new CustomerDTO();

        dto.setCustomerId(customer.getCustomerId());
        dto.setStoreId(customer.getStoreId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setAddressId(customer.getAddressId());
        dto.setActivebool(customer.getActivebool());

        return dto;
    }

    public static Customer toEntity(CustomerDTO dto) {

        Customer customer = new Customer();

        customer.setStoreId(dto.getStoreId());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setAddressId(dto.getAddressId());
        customer.setActivebool(dto.getActivebool());

        // Yeni müşterinin oluşturulma tarihi
        customer.setCreateDate(LocalDate.now());

        return customer;
    }
}