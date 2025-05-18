package com.bootcoin.service;

import com.bootcoin.model.ExchangeRateCache;

import reactor.core.publisher.Mono;

public interface ExchangeRateCacheService {
	Mono<Void> saveRate(ExchangeRateCache rate);
	Mono<ExchangeRateCache> getRate();
}
