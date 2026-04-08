package com.btgpactual.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppConfigTest {

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AppConfig appConfig;

    @Test
    void testAuthenticationProvider() {
        AuthenticationProvider provider = appConfig.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        AuthenticationManager managerMock = mock(AuthenticationManager.class);
        when(config.getAuthenticationManager()).thenReturn(managerMock);

        AuthenticationManager manager = appConfig.authenticationManager(config);
        assertNotNull(manager);
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = appConfig.passwordEncoder();
        assertNotNull(encoder);
    }
}
