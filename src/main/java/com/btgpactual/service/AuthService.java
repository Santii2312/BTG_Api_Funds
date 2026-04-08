package com.btgpactual.service;

import com.btgpactual.domain.NotificationPreference;
import com.btgpactual.domain.User;
import com.btgpactual.dto.AuthRequest;
import com.btgpactual.dto.AuthResponse;
import com.btgpactual.repository.UserRepository;
import com.btgpactual.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        return AuthResponse.builder().token(jwtToken).build();
    }

    public void createDemoUser() {
        if (userRepository.findByUsername("demo").isEmpty()) {
            User user = User.builder()
                    .username("demo")
                    .password(passwordEncoder.encode("password"))
                    .roles(Set.of("USER"))
                    .balance(new BigDecimal("500000"))
                    .email("demo@test.com")
                    .phone("3001234567")
                    .notificationPreference(NotificationPreference.EMAIL)
                    .build();
            userRepository.save(user);
        }
    }
}
