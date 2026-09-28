package com.halil.dvdrental.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final StaffUserDetailsService staffUserDetailsService;
    private final CustomerUserDetailsService customerUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private DaoAuthenticationProvider buildProvider(UserDetailsService uds) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(buildProvider(staffUserDetailsService));
        http.authenticationProvider(buildProvider(customerUserDetailsService));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login.xhtml", "/jakarta.faces.resource/**").permitAll()
                .requestMatchers("/staff/**").hasRole("STAFF")
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                .requestMatchers("/customers.xhtml").hasRole("STAFF")
                .requestMatchers("/filmler.xhtml").authenticated()
                .anyRequest().permitAll()
        );

        http.formLogin(form -> form
                .loginPage("/login.xhtml")
                .loginProcessingUrl("/login-process")
                .successHandler(new RoleBasedSuccessHandler())
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.xhtml?logout")
                .invalidateHttpSession(true)
                .permitAll()
        );

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}