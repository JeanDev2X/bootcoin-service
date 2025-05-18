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
@Document(collection = "exchange_rates")
public class ExchangeRate {
	@Id
    private String id;
    private BigDecimal buyRate;  // soles to bootcoin
    private BigDecimal sellRate; // bootcoin to soles
    private LocalDate updatedAt;
}
