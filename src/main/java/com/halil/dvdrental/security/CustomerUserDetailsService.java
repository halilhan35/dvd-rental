package com.halil.dvdrental.security;

import com.halil.dvdrental.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("customerUserDetailsService")
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return customerRepository.findByEmail(email)
                .filter(customer -> customer.getPassword() != null)
                .map(customer -> new AppUserDetails(
                        customer.getCustomerId(),
                        customer.getEmail(),
                        customer.getPassword(),
                        "CUSTOMER",
                        customer.getFirstName(),
                        customer.getLastName()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Customer bulunamadı: " + email));
    }
}