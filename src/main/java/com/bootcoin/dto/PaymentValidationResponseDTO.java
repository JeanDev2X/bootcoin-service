package com.bootcoin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentValidationResponseDTO {
	private String transactionRef;
    private boolean valid;
    private String reason; // si `valid == false`, puede indicar "Saldo insuficiente" o "Cuenta no existe"
}
