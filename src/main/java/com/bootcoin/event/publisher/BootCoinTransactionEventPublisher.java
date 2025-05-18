package com.bootcoin.event.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.bootcoin.dto.BootCoinTransactionEventDTO;
import com.bootcoin.model.BootCoinTransaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootCoinTransactionEventPublisher {//Publicar Evento: Solicitud de Compra
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
    private static final String TOPIC = "bootcoin-transaction-events";

    // Evento al solicitar compra
    public void publishTransactionRequested(BootCoinTransaction transaction) {
    	BootCoinTransactionEventDTO eventDTO = BootCoinTransactionEventDTO.builder()
    		    .transactionRef(transaction.getTransactionRef())
    		    .buyerPhoneNumber(transaction.getBuyerPhoneNumber())
    		    .sellerPhoneNumber(transaction.getSellerPhoneNumber())
    		    .amountInSoles(transaction.getAmountInSoles())
    		    .amountInBootCoins(transaction.getAmountInBootCoins())
    		    .paymentMode(transaction.getPaymentMode())
    		    .status(transaction.getStatus())
    		    .createdAt(transaction.getCreatedAt())
    		    .transactionType(transaction.getTransactionType())
    		    .exchangeRateAtRequest(transaction.getExchangeRateAtRequest()) // <- AÑADIDO
    		    .build();

        log.info("Publishing PENDING BootCoinTransaction event to Kafka: {}", eventDTO);
        kafkaTemplate.send(TOPIC, eventDTO);
    }

    public void publishTransactionConfirmed(BootCoinTransaction transaction) {
    	BootCoinTransactionEventDTO eventDTO = BootCoinTransactionEventDTO.builder()
    		    .transactionRef(transaction.getTransactionRef())
    		    .buyerPhoneNumber(transaction.getBuyerPhoneNumber())
    		    .sellerPhoneNumber(transaction.getSellerPhoneNumber())
    		    .amountInSoles(transaction.getAmountInSoles())
    		    .amountInBootCoins(transaction.getAmountInBootCoins())
    		    .paymentMode(transaction.getPaymentMode())
    		    .status(transaction.getStatus())
    		    .createdAt(transaction.getCreatedAt())
    		    .transactionType(transaction.getTransactionType())
    		    .exchangeRateAtRequest(transaction.getExchangeRateAtRequest()) // <- AÑADIDO
    		    .build();

        log.info("Publishing CONFIRMED BootCoinTransaction event to Kafka: {}", eventDTO);
        kafkaTemplate.send(TOPIC, eventDTO);
    }
    
}
