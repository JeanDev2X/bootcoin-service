package com.bootcoin.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.bootcoin.model.ExchangeRate;

import reactor.core.publisher.Mono;

public interface ExchangeRateRepository extends ReactiveMongoRepository<ExchangeRate, String>{

	Mono<ExchangeRate> findTopByOrderByUpdatedAtDesc();

}
