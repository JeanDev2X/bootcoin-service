package com.bootcoin.model;

import java.math.BigDecimal;

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
@Document(collection = "bootcoin_wallets")
public class BootCoinWallet {
	@Id
    private String id;
    private String documentNumber;
    private String documentType; // DNI, CEX, Pasaporte
    private String phoneNumber;
    private String email;
    
    private BigDecimal bootCoinBalance = BigDecimal.ZERO; // Saldo en BootCoins
    private BigDecimal solesBalance = BigDecimal.ZERO; // saldo en moneda nacional
    
    private String linkedYankiPhoneNumber; // Para pagos con YANKI
    private String linkedAccountNumber;    // Para pagos por transferencia
}
