package com.btgpactual;

import com.btgpactual.repository.FundRepository;
import com.btgpactual.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FondosApiApplicationTest {

    @Test
    void testSeedDatabaseEmpty() throws Exception {
        FondosApiApplication app = new FondosApiApplication();
        FundRepository fundRepository = mock(FundRepository.class);
        AuthService authService = mock(AuthService.class);

        when(fundRepository.count()).thenReturn(0L);

        CommandLineRunner runner = app.seedDatabase(fundRepository, authService);
        runner.run();

        verify(authService, times(1)).createDemoUser();
        verify(fundRepository, times(1)).saveAll(any());
    }

    @Test
    void testSeedDatabaseNotEmpty() throws Exception {
        FondosApiApplication app = new FondosApiApplication();
        FundRepository fundRepository = mock(FundRepository.class);
        AuthService authService = mock(AuthService.class);

        when(fundRepository.count()).thenReturn(5L);

        CommandLineRunner runner = app.seedDatabase(fundRepository, authService);
        runner.run();

        verify(authService, times(1)).createDemoUser();
        verify(fundRepository, never()).saveAll(any());
    }
}
