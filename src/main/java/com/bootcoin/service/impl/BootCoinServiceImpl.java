package com.bootcoin.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import com.bootcoin.config.RedisConfig;
import com.bootcoin.event.listener.BootCoinTransactionEventListener;
import com.bootcoin.event.publisher.BootCoinTransactionEventPublisher;
import com.bootcoin.model.BootCoinTransaction;
import com.bootcoin.model.BootCoinWallet;
import com.bootcoin.model.ExchangeRate;
import com.bootcoin.model.ExchangeRateCache;
import com.bootcoin.repository.BootCoinTransactionRepository;
import com.bootcoin.repository.BootCoinWalletRepository;
import com.bootcoin.repository.ExchangeRateRepository;
import com.bootcoin.service.BootCoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BootCoinServiceImpl implements BootCoinService{
	
	private final ExchangeRateRepository exchangeRateRepository;
    private final BootCoinWalletRepository bootCoinWalletRepository;
    private final BootCoinTransactionRepository bootCoinTransactionRepository;
    private final BootCoinTransactionEventPublisher eventPublisher;
    
    @Autowired
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private static final String EXCHANGE_RATE_CACHE_KEY = "exchange-rate";

	@Override
	public Mono<ExchangeRate> updateExchangeRate(ExchangeRate rate) {
		if (rate.getUpdatedAt() == null) {
	        rate.setUpdatedAt(LocalDate.now());
	    }

	    ExchangeRateCache cache = new ExchangeRateCache(rate.getBuyRate(), rate.getSellRate());

	    return redisTemplate
	            .opsForValue()
	            .set(EXCHANGE_RATE_CACHE_KEY, cache)
	            .then(exchangeRateRepository.save(rate));
	}

	@Override
	public Mono<ExchangeRate> getExchangeRate() {
		return redisTemplate
	            .opsForValue()
	            .get(EXCHANGE_RATE_CACHE_KEY)
	            .cast(ExchangeRateCache.class)
	            .map(cache -> ExchangeRate.builder()
	                    .buyRate(cache.getBuyRate())
	                    .sellRate(cache.getSellRate())
	                    .updatedAt(LocalDate.now())
	                    .build())
	            .switchIfEmpty(exchangeRateRepository
	                .findAll()
	                .last() //la más reciente
	                .flatMap(rate -> {
	                    ExchangeRateCache cache = new ExchangeRateCache(rate.getBuyRate(), rate.getSellRate());
	                    return redisTemplate
	                            .opsForValue()
	                            .set(EXCHANGE_RATE_CACHE_KEY, cache)
	                            .thenReturn(rate);
	                }));
	}

	@Override
	public Mono<BootCoinWallet> createWallet(BootCoinWallet wallet) {
		return bootCoinWalletRepository.findByPhoneNumber(wallet.getPhoneNumber())
                .switchIfEmpty(bootCoinWalletRepository.save(wallet));
	}

	@Override
	public Mono<BootCoinWallet> getWalletByPhoneNumber(String phoneNumber) {
		return bootCoinWalletRepository.findByPhoneNumber(phoneNumber);
	}
	//REQ3
	/**
     * Procesa la solicitud de transacción: guarda la transacción con estado PENDING
     * y publica un evento de "REQUESTED" a Kafka incluyendo transactionType.
     */
	@Override
	public Mono<BootCoinTransaction> requestTransaction(BootCoinTransaction transaction) {
		String type = transaction.getTransactionType();

	    if (!"BUY".equalsIgnoreCase(type) && !"SELL".equalsIgnoreCase(type)) {
	        return Mono.error(new IllegalArgumentException("transactionType inválido"));
	    }

	    if ("BUY".equalsIgnoreCase(type) && transaction.getAmountInSoles() == null) {
	        return Mono.error(new IllegalArgumentException("amountInSoles requerido para transacción BUY"));
	    }

	    if ("SELL".equalsIgnoreCase(type) && transaction.getAmountInBootCoins() == null) {
	        return Mono.error(new IllegalArgumentException("amountInBootCoins requerido para transacción SELL"));
	    }

	    return exchangeRateRepository.findTopByOrderByUpdatedAtDesc()
	        .flatMap(rate -> {
	            BigDecimal rateToUse = "BUY".equalsIgnoreCase(type) ? rate.getBuyRate() : rate.getSellRate();

	            transaction.setExchangeRateAtRequest(rateToUse);
	            transaction.setStatus("PENDING");
	            transaction.setTransactionRef(UUID.randomUUID().toString());
	            transaction.setCreatedAt(LocalDate.now());

	            return bootCoinTransactionRepository.save(transaction)
	                .doOnSuccess(savedTx -> {
	                    log.info("Transacción guardada con tipo de cambio: {}", rateToUse);
	                    eventPublisher.publishTransactionRequested(savedTx);
	                });
	        });
	}

	@Override
	public Flux<BootCoinTransaction> getPendingTransactions() {
		return bootCoinTransactionRepository.findByStatus("PENDING");
	}

	@Override
	public Mono<BootCoinTransaction> confirmTransaction(String transactionRef, String phoneNumber) {
		return bootCoinTransactionRepository.findByTransactionRef(transactionRef)
		        .flatMap(tx -> {
		            tx.setStatus("CONFIRMED");

		            if ("BUY".equalsIgnoreCase(tx.getTransactionType())) {
		                // El vendedor acepta la compra → actualizamos seller
		                tx.setSellerPhoneNumber(phoneNumber);
		            } else if ("SELL".equalsIgnoreCase(tx.getTransactionType())) {
		                // El comprador acepta la venta → actualizamos buyer
		                tx.setBuyerPhoneNumber(phoneNumber);
		            } else {
		                return Mono.error(new IllegalStateException("Tipo de transacción inválido: " + tx.getTransactionType()));
		            }

		            return bootCoinTransactionRepository.save(tx)
		                .doOnSuccess(eventPublisher::publishTransactionConfirmed);
		        });
	}

}
