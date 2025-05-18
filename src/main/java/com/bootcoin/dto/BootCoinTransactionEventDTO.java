package com.bootcoin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BootCoinTransactionEventDTO {
	private String transactionRef;
    private String buyerPhoneNumber;//Número de teléfono del comprador
    private String sellerPhoneNumber;//Número de teléfono del vendedor
    private BigDecimal amountInSoles; // Monto en soles que el comprador paga
    private BigDecimal amountInBootCoins;
    private BigDecimal exchangeRateAtRequest;
    private String paymentMode;       // YANKI o TRANSFER
    private String status;            // CONFIRMED,PENDIENTES,COMPLETO
    private LocalDate createdAt;
    private String transactionType; // valores posibles: "BUY" o "SELL"
}
