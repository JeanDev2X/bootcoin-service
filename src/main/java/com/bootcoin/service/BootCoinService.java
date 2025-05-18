package com.bootcoin.service;

import com.bootcoin.model.BootCoinTransaction;
import com.bootcoin.model.BootCoinWallet;
import com.bootcoin.model.ExchangeRate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootCoinService {
	Mono<ExchangeRate> updateExchangeRate(ExchangeRate rate);
    Mono<ExchangeRate> getExchangeRate();

    Mono<BootCoinWallet> createWallet(BootCoinWallet wallet);
    Mono<BootCoinWallet> getWalletByPhoneNumber(String phoneNumber);

    Mono<BootCoinTransaction> requestTransaction(BootCoinTransaction transaction);
    Flux<BootCoinTransaction> getPendingTransactions();

    Mono<BootCoinTransaction> confirmTransaction(String transactionId, String sellerPhoneNumber);
}
