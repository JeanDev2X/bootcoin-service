package com.bootcoin.repository;

import com.bootcoin.model.BootCoinWallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface BootCoinWalletRepository extends ReactiveMongoRepository<BootCoinWallet, String>{
	Mono<BootCoinWallet> findByPhoneNumber(String phoneNumber);
}
