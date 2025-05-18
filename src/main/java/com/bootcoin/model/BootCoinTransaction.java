package com.bootcoin.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bootcoin_transactions")
public class BootCoinTransaction {
	@Id
    private String id;
    private String buyerPhoneNumber;
    private String sellerPhoneNumber;
    private BigDecimal amountInSoles;// usado en transacciones BUY
    private BigDecimal amountInBootCoins;// usado en transacciones SELL
    private BigDecimal exchangeRateAtRequest; // Tasa de cambio aplicada al momento de crear la transacción
    private String paymentMode; // YANKI or TRANSFER
    private String status; // PENDING, COMPLETED, FAILED
    private LocalDate createdAt;
    private String transactionRef;
    private String transactionType; // valores posibles: "BUY" o "SELL"
}
