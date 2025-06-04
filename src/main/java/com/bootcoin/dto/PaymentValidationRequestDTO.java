package com.bootcoin.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentValidationRequestDTO {
	private String transactionRef;
    //private String actorPhoneNumber; // quien acepta la transacción
	private String actorIdentifier;
    private String paymentMode;      // YANKI o TRANSFER
    private String transactionType;  // BUY o SELL

    private BigDecimal amountInSoles;     // monto a validar (en soles)
    private String accountNumber;         // si es TRANSFER
    private String phoneNumber;           // si es YANKI
}
