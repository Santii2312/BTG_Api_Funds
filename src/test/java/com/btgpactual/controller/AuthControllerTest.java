package com.btgpactual.controller;

import com.btgpactual.dto.AuthRequest;
import com.btgpactual.dto.AuthResponse;
import com.btgpactual.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLogin() {
        AuthRequest request = new AuthRequest("demo", "password");
        AuthResponse response = AuthResponse.builder().token("token").build();
        
        when(authService.authenticate(request)).thenReturn(response);
        
        ResponseEntity<AuthResponse> result = authController.login(request);
        
        assertEquals(200, result.getStatusCode().value());
        assertEquals("token", result.getBody().getToken());
    }
}
