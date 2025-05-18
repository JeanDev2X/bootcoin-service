package com.bootcoin.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateCache {
	private BigDecimal buyRate;
    private BigDecimal sellRate;
}
