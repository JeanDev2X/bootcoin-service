//package com.bootcoin.event.listener;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import com.bootcoin.dto.BootCoinTransactionEventDTO;
//import com.bootcoin.model.BootCoinTransaction;
//import com.bootcoin.model.BootCoinWallet;
//import com.bootcoin.model.ExchangeRate;
//import com.bootcoin.repository.BootCoinTransactionRepository;
//import com.bootcoin.repository.BootCoinWalletRepository;
//import com.bootcoin.repository.ExchangeRateRepository;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class BootCoinTransactionEventListener {
//	
//	private final BootCoinWalletRepository walletRepository;
//	private final BootCoinTransactionRepository transactionRepository;
//	private final ExchangeRateRepository exchangeRateRepository;
//
//	@KafkaListener(
//		topics = "bootcoin-transaction-events",
//		groupId = "bootcoin-service",
//		containerFactory = "bootCoinTransactionEventKafkaListenerContainerFactory"
//	)
//	public void handleTransactionEvent(BootCoinTransactionEventDTO event) {
//		log.info("Received BootCoinTransaction event from Kafka: {}", event);
//
//		if (!"CONFIRMED".equalsIgnoreCase(event.getStatus())) return;
//
//		String transactionType = event.getTransactionType();
//		if (transactionType == null || (!transactionType.equalsIgnoreCase("BUY") && !transactionType.equalsIgnoreCase("SELL"))) {
//			log.error("Transaction type inválido o nulo: {}", transactionType);
//			return;
//		}
//
//		Mono<ExchangeRate> rateMono = exchangeRateRepository.findTopByOrderByUpdatedAtDesc();
//		Mono<BootCoinWallet> sellerMono = walletRepository.findByPhoneNumber(event.getSellerPhoneNumber());
//		Mono<BootCoinWallet> buyerMono = walletRepository.findByPhoneNumber(event.getBuyerPhoneNumber());
//
//		Mono.zip(rateMono, sellerMono, buyerMono)
//			.flatMap(tuple -> {
//				ExchangeRate rate = tuple.getT1();
//				BootCoinWallet seller = tuple.getT2();
//				BootCoinWallet buyer = tuple.getT3();
//
//				if ("BUY".equalsIgnoreCase(transactionType)) {
//					BigDecimal amountInSoles = event.getAmountInSoles();
//					if (amountInSoles == null) return Mono.error(new IllegalArgumentException("amountInSoles no puede ser nulo en una transacción BUY"));
//
//					BigDecimal bootCoinsToTransfer = amountInSoles.divide(rate.getBuyRate(), 8, RoundingMode.HALF_UP);
//					log.info("BUY: {} soles / {} tasa = {} BootCoins", amountInSoles, rate.getBuyRate(), bootCoinsToTransfer);
//
//					// Validaciones
//					if (seller.getBootCoinBalance().compareTo(bootCoinsToTransfer) < 0) {
//						return Mono.error(new IllegalStateException("El vendedor no tiene suficientes BootCoins"));
//					}
//					if (buyer.getSolesBalance().compareTo(amountInSoles) < 0) {
//						return Mono.error(new IllegalStateException("El comprador no tiene suficientes soles"));
//					}
//
//					// Realizar intercambio
//					seller.setBootCoinBalance(seller.getBootCoinBalance().subtract(bootCoinsToTransfer));
//					seller.setSolesBalance(seller.getSolesBalance().add(amountInSoles));
//
//					buyer.setBootCoinBalance(buyer.getBootCoinBalance().add(bootCoinsToTransfer));
//					buyer.setSolesBalance(buyer.getSolesBalance().subtract(amountInSoles));
//
//				} else if ("SELL".equalsIgnoreCase(transactionType)) {
//					BigDecimal amountInBootCoins = event.getAmountInBootCoins();
//					if (amountInBootCoins == null) return Mono.error(new IllegalArgumentException("amountInBootCoins no puede ser nulo en una transacción SELL"));
//
//					BigDecimal amountInSoles = amountInBootCoins.multiply(rate.getSellRate());
//					log.info("SELL: {} BootCoins * {} tasa = {} soles", amountInBootCoins, rate.getSellRate(), amountInSoles);
//
//					// Validaciones
//					if (seller.getBootCoinBalance().compareTo(amountInBootCoins) < 0) {
//						return Mono.error(new IllegalStateException("El vendedor no tiene suficientes BootCoins"));
//					}
//					if (buyer.getSolesBalance().compareTo(amountInSoles) < 0) {
//						return Mono.error(new IllegalStateException("El comprador no tiene suficientes soles"));
//					}
//
//					// Realizar intercambio
//					seller.setBootCoinBalance(seller.getBootCoinBalance().subtract(amountInBootCoins));
//					seller.setSolesBalance(seller.getSolesBalance().add(amountInSoles));
//
//					buyer.setBootCoinBalance(buyer.getBootCoinBalance().add(amountInBootCoins));
//					buyer.setSolesBalance(buyer.getSolesBalance().subtract(amountInSoles));
//				}
//
//				// Guardar ambos wallets
//				return walletRepository.saveAll(List.of(seller, buyer)).then();
//			})
//			.then(actualizarTransaccionComoCompleta(event.getTransactionRef()))
//			.doOnSuccess(r -> log.info("Transacción COMPLETADA: {}", event.getTransactionRef()))
//			.doOnError(e -> log.error("Error en procesamiento de transacción: {}", e.getMessage()))
//			.subscribe();
//	}
//
//	private Mono<BootCoinTransaction> actualizarTransaccionComoCompleta(String transactionRef) {
//		return transactionRepository.findByTransactionRef(transactionRef)
//			.flatMap(tx -> {
//				tx.setStatus("COMPLETED");
//				return transactionRepository.save(tx);
//			});
//	}
//	
//}
