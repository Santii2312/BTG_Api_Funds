package com.btgpactual;

import com.btgpactual.domain.Fund;
import com.btgpactual.repository.FundRepository;
import com.btgpactual.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class FondosApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FondosApiApplication.class, args);
    }


    // Seed inicial: crea los 5 fondos del catálogo y el usuario demo si no existen.
    // Esto se ejecuta una sola vez en el arranque de la aplicación.
    @Bean
    public CommandLineRunner seedDatabase(FundRepository fundRepository, AuthService authService) {
        return args -> {
            // Crear usuario demo si no existe
            authService.createDemoUser();

            // Crear fondos si la colección esta vacía
            if (fundRepository.count() == 0) {
                fundRepository.saveAll(List.of(
                        Fund.builder().nombre("FPV_BTG_PACTUAL_RECAUDADORA").montoMinimo(new BigDecimal("75000"))
                                .categoria("FPV").build(),
                        Fund.builder().nombre("FPV_BTG_PACTUAL_ECOPETROL").montoMinimo(new BigDecimal("125000"))
                                .categoria("FPV").build(),
                        Fund.builder().nombre("DEUDAPRIVADA").montoMinimo(new BigDecimal("50000")).categoria("FIC")
                                .build(),
                        Fund.builder().nombre("FDO-ACCIONES").montoMinimo(new BigDecimal("250000")).categoria("FIC")
                                .build(),
                        Fund.builder().nombre("FPV_BTG_PACTUAL_DINAMICA").montoMinimo(new BigDecimal("100000"))
                                .categoria("FPV").build()));
                System.out.println("[SEED] Fondos inicializados correctamente.");
            }
        };
    }
}
