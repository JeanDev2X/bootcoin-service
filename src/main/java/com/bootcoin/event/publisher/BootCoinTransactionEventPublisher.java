//package com.bootcoin.event.publisher;
//
//import java.math.BigDecimal;
//
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//import com.bootcoin.dto.BootCoinTransactionEventDTO;
//import com.bootcoin.dto.PaymentValidationRequestDTO;
//import com.bootcoin.model.BootCoinTransaction;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class BootCoinTransactionEventPublisher {//Publicar Evento: Solicitud de Compra
//	
//	private final KafkaTemplate<String, Object> kafkaTemplate;
//
//	private static final String PAYMENT_VALIDATION_TOPIC = "payment-validation-request";
//	
//    public void publishPaymentValidationRequest(BootCoinTransaction transaction) {
//    	BigDecimal amountInSoles = "BUY".equalsIgnoreCase(transaction.getTransactionType())
//    	        ? transaction.getAmountInSoles()
//    	        : transaction.getAmountInBootCoins().multiply(transaction.getExchangeRateAtRequest());
//
//    	    // Identificador del actor que realizará el pago (teléfono o cuenta, según el modo)
//    	    String actorIdentifier = "YANKI".equalsIgnoreCase(transaction.getPaymentMode())
//    	        ? ("BUY".equalsIgnoreCase(transaction.getTransactionType())
//    	            ? transaction.getBuyerPhoneNumber()
//    	            : transaction.getSellerPhoneNumber())
//    	        : ("BUY".equalsIgnoreCase(transaction.getTransactionType())
//    	            ? transaction.getBuyerAccountNumber()
//    	            : transaction.getSellerAccountNumber());
//
//    	    PaymentValidationRequestDTO dto = PaymentValidationRequestDTO.builder()
//    	        .transactionRef(transaction.getTransactionRef())
//    	        .amountInSoles(amountInSoles)
//    	        .actorIdentifier(actorIdentifier)  // Este es el campo unificado
//    	        .paymentMode(transaction.getPaymentMode())
//    	        .phoneNumber(transaction.getBuyerPhoneNumber())
//    	        .accountNumber(transaction.getBuyerAccountNumber())
//    	        .transactionType(transaction.getTransactionType())
//    	        .build();
//
//    	    log.info("Publishing payment validation request: {}", dto);
//    	    kafkaTemplate.send(PAYMENT_VALIDATION_TOPIC, dto);
//    }
//    
//    public void publishTransactionConfirmed(BootCoinTransaction transaction) {
//    	BootCoinTransactionEventDTO eventDTO = BootCoinTransactionEventDTO.builder()
//    		    .transactionRef(transaction.getTransactionRef())
//    		    .buyerPhoneNumber(transaction.getBuyerPhoneNumber())
//    		    .sellerPhoneNumber(transaction.getSellerPhoneNumber())
//    		    .amountInBootCoins(transaction.getAmountInBootCoins())
//    		    .paymentMode(transaction.getPaymentMode())
//    		    .status(transaction.getStatus())
//    		    .createdAt(transaction.getCreatedAt())
//    		    .transactionType(transaction.getTransactionType())
//    		    .exchangeRateAtRequest(transaction.getExchangeRateAtRequest()) // AÑADIDO
//    		    .build();
//
//        log.info("Publishing CONFIRMED BootCoinTransaction event to Kafka: {}", eventDTO);
//        kafkaTemplate.send(PAYMENT_VALIDATION_TOPIC, eventDTO);
//    }
//    
//}
