package com.bootcoin.event.listener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bootcoin.dto.PaymentValidationResponseDTO;
import com.bootcoin.model.BootCoinWallet;
import com.bootcoin.model.ExchangeRate;
import com.bootcoin.repository.BootCoinTransactionRepository;
import com.bootcoin.repository.BootCoinWalletRepository;
import com.bootcoin.repository.ExchangeRateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootCoinTransactionValidationListener {
	
	private final BootCoinWalletRepository walletRepository;
	private final BootCoinTransactionRepository transactionRepository;
	private final ExchangeRateRepository exchangeRateRepository;

	@KafkaListener(
		topics = "payment-validation-response",
		groupId = "bootcoin-service",
		containerFactory = "paymentValidationResponseKafkaListenerContainerFactory"
	)
	public void handleValidationResponse(PaymentValidationResponseDTO response) {
		log.info("Received PaymentValidationResponse from Kafka: {}", response);

		transactionRepository.findByTransactionRef(response.getTransactionRef())
	    .flatMap(tx -> {
	        if (!response.isValid()) {
	            log.warn("Validación fallida para transacción {}: {}", tx.getTransactionRef(), response.getReason());
	            tx.setStatus("FAILED");
	            return transactionRepository.save(tx);
	        }

	        BigDecimal rateAtRequest = tx.getExchangeRateAtRequest();
	        
	        Mono<BootCoinWallet> sellerMono = walletRepository.findByPhoneNumber(tx.getSellerPhoneNumber());
	        Mono<BootCoinWallet> buyerMono = walletRepository.findByPhoneNumber(tx.getBuyerPhoneNumber());

	        return Mono.zip(
	            sellerMono.switchIfEmpty(Mono.error(new IllegalStateException("Seller no encontrado"))),
	            buyerMono.switchIfEmpty(Mono.error(new IllegalStateException("Buyer no encontrado")))
	        ).flatMap(tuple -> {
	            BootCoinWallet seller = tuple.getT1();
	            BootCoinWallet buyer = tuple.getT2();
	            String type = tx.getTransactionType();

	            if ("BUY".equalsIgnoreCase(type)) {
	                BigDecimal soles = tx.getAmountInSoles();
	                BigDecimal bootcoins = soles.divide(rateAtRequest, 8, RoundingMode.HALF_UP);
	                log.info("BUY: {} soles / {} tasa = {} BootCoins", soles, rateAtRequest, bootcoins);
	                if (seller.getBootCoinBalance().compareTo(bootcoins) < 0) {
	                    return Mono.error(new IllegalStateException("El vendedor no tiene suficientes BootCoins"));//Saldo insuficiente del vendedor
	                }
	                // Actualizar balances
	                seller.setBootCoinBalance(seller.getBootCoinBalance().subtract(bootcoins));
	                buyer.setBootCoinBalance(buyer.getBootCoinBalance().add(bootcoins));

	            } else if ("SELL".equalsIgnoreCase(type)) {
	                BigDecimal bootcoins = tx.getAmountInBootCoins();
	                BigDecimal soles = bootcoins.multiply(rateAtRequest);
	                log.info("SELL: {} BootCoins * {} tasa = {} soles", bootcoins, rateAtRequest, soles);
	                
	                if (seller.getBootCoinBalance().compareTo(bootcoins) < 0) {
	                    return Mono.error(new IllegalStateException("Saldo insuficiente del vendedor"));
	                }

	                seller.setBootCoinBalance(seller.getBootCoinBalance().subtract(bootcoins));
	                buyer.setBootCoinBalance(buyer.getBootCoinBalance().add(bootcoins));
	            }

	            tx.setStatus("COMPLETED");
	            return walletRepository.saveAll(List.of(seller, buyer))
	                .then(transactionRepository.save(tx));
	        });
	    })
	    .doOnSuccess(tx -> log.info("Transacción COMPLETADA: {}", tx.getTransactionRef()))
	    .doOnError(e -> log.error("Error procesando validación para transacción {}: {}", response.getTransactionRef(), e.getMessage()))
	    .onErrorResume(e ->
	        transactionRepository.findByTransactionRef(response.getTransactionRef())
	            .flatMap(tx -> {
	                tx.setStatus("FAILED");
	                return transactionRepository.save(tx);
	            })
	    )
	    .subscribe();
	}
	
}
