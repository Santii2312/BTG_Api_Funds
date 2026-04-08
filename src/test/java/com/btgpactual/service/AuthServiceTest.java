package com.btgpactual.service;

import com.btgpactual.domain.User;
import com.btgpactual.dto.AuthRequest;
import com.btgpactual.dto.AuthResponse;
import com.btgpactual.repository.UserRepository;
import com.btgpactual.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    void testAuthenticate() {
        AuthRequest request = new AuthRequest("demo", "password");
        UserDetails mockUserDetails = mock(UserDetails.class);
        
        when(userDetailsService.loadUserByUsername("demo")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("fake-jwt-token");

        AuthResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(userDetailsService, times(1)).loadUserByUsername("demo");
        verify(jwtService, times(1)).generateToken(mockUserDetails);
    }

    @Test
    void testCreateDemoUser_WhenNotExists() {
        when(userRepository.findByUsername("demo")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        authService.createDemoUser();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertEquals("demo", savedUser.getUsername());
        assertEquals("encoded-password", savedUser.getPassword());
    }

    @Test
    void testCreateDemoUser_WhenExists() {
        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(new User()));
        
        authService.createDemoUser();
        
        verify(userRepository, times(0)).save(any());
    }
}
