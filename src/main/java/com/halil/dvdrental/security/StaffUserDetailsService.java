package com.halil.dvdrental.security;

import com.halil.dvdrental.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("staffUserDetailsService")
@RequiredArgsConstructor
public class StaffUserDetailsService implements UserDetailsService {

    private final StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return staffRepository.findByUsername(username)
                .map(staff -> new AppUserDetails(
                        staff.getStaffId(),
                        staff.getUsername(),
                        staff.getPassword(),
                        "STAFF",
                        staff.getFirstName(),
                        staff.getLastName()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Staff bulunamadı: " + username));
    }
}