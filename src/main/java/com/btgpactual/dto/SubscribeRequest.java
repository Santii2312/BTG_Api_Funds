package com.btgpactual.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeRequest {
    @NotBlank(message = "El ID del fondo es obligatorio")
    private String fundId;
    
    @NotNull(message = "El monto a vincular es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto a vincular debe ser mayor a 0")
    private BigDecimal amount;
}
