package com.bootcoin.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.bootcoin.model.BootCoinTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootCoinTransactionRepository extends ReactiveMongoRepository<BootCoinTransaction, String>{

	Flux<BootCoinTransaction> findByStatus(String string);
	Mono<BootCoinTransaction> findByTransactionRef(String transactionRef);

}
