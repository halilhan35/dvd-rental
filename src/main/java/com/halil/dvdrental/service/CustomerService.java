package com.halil.dvdrental.service;

import com.halil.dvdrental.dto.CustomerDTO;
import com.halil.dvdrental.entity.Customer;
import com.halil.dvdrental.mapper.CustomerMapper;
import com.halil.dvdrental.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerDTO> getAllCustomerDTOs() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toDTO)
                .toList();
    }

    public CustomerDTO getCustomerDTOById(Integer id) {
        return customerRepository.findById(id)
                .map(CustomerMapper::toDTO)
                .orElse(null);
    }

    @PreAuthorize("hasRole('STAFF')")
    public CustomerDTO saveCustomerDTO(CustomerDTO dto) {

        System.out.println(">>> SERVICE SAVE BAŞLADI");

        System.out.println(">>> ID: " + dto.getCustomerId());
        System.out.println(">>> Store ID: " + dto.getStoreId());
        System.out.println(">>> Address ID: " + dto.getAddressId());
        System.out.println(">>> Active: " + dto.getActivebool());

        Customer customer;

        if (dto.getCustomerId() == null) {

            System.out.println(">>> YENİ CUSTOMER OLUŞTURULUYOR");

            customer = CustomerMapper.toEntity(dto);

            System.out.println(">>> ENTITY OLUŞTU");
            System.out.println(">>> Entity Store ID: " + customer.getStoreId());
            System.out.println(">>> Entity Address ID: " + customer.getAddressId());
            System.out.println(">>> Entity Create Date: " + customer.getCreateDate());

            customer = customerRepository.save(customer);

            System.out.println(">>> REPOSITORY SAVE TAMAMLANDI");

        } else {

            System.out.println(">>> MEVCUT CUSTOMER GÜNCELLENİYOR");

            customer = customerRepository
                    .findById(dto.getCustomerId())
                    .orElse(null);

            if (customer == null) {
                System.out.println(">>> CUSTOMER BULUNAMADI");
                return null;
            }

            customer.setStoreId(dto.getStoreId());
            customer.setFirstName(dto.getFirstName());
            customer.setLastName(dto.getLastName());
            customer.setEmail(dto.getEmail());
            customer.setAddressId(dto.getAddressId());
            customer.setActivebool(dto.getActivebool());

            customer = customerRepository.save(customer);
        }

        System.out.println(">>> DTO'YA DÖNÜŞ YAPILIYOR");

        return CustomerMapper.toDTO(customer);
    }

    @PreAuthorize("hasRole('STAFF')")
    public boolean deleteCustomer(Integer id) {

        long rentalCount =
                customerRepository.countRentalsByCustomerId(id);

        if (rentalCount > 0) {
            return false;
        }

        customerRepository.deleteById(id);
        return true;
    }
}