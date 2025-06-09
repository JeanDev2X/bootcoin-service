package com.bootcoin.op.dto;

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
public class ConfirmTransactionRequest {
	private String transactionRef;
    //private String actorPhoneNumber;
	private String actorIdentifier;
    private String accountNumber; // opcional, si es transferencia
    private String phoneNumber;   // opcional, si es Yanki
}
