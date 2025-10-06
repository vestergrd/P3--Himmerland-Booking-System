package com.auu_sw3_6.Himmerland_booking_software.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auu_sw3_6.Himmerland_booking_software.api.model.User;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.AdminRepository;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.TenantRepository;
import com.auu_sw3_6.Himmerland_booking_software.config.security.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final TenantRepository tenantRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public CustomUserDetailsService(TenantRepository tenantRepository, AdminRepository adminRepository) {
        this.tenantRepository = tenantRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<? extends User> user = tenantRepository.findByUsername(username);
        String role = "ROLE_TENANT";

        // If user is not found in the tenant repository, search in the admin repository
        if (user.isEmpty()) {
            user = adminRepository.findByUsername(username);
            if (user.isPresent()) {
                role = "ROLE_ADMIN";
            }
        }

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        logger.info("User found: {} with roles: {}", user.get().getUsername(), role);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return new CustomUserDetails(user.get(), authorities);
    }
}
