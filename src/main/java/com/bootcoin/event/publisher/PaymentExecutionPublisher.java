package com.bootcoin.event.publisher;

import java.math.BigDecimal;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bootcoin.dto.PaymentExecutionRequestDTO;
import com.bootcoin.model.BootCoinTransaction;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentExecutionPublisher {
	private final KafkaTemplate<String, PaymentExecutionRequestDTO> kafkaTemplate;

    public void publishExecutionRequest(BootCoinTransaction tx) {
        BigDecimal amountInSoles = "BUY".equalsIgnoreCase(tx.getTransactionType())
            ? tx.getAmountInSoles()
            : tx.getAmountInBootCoins().multiply(tx.getExchangeRateAtRequest());

        String actorIdentifier = "BUY".equalsIgnoreCase(tx.getTransactionType())
            ? tx.getBuyerPhoneNumber() // puede ser número de cuenta en TRANSFER
            : tx.getSellerPhoneNumber();

        PaymentExecutionRequestDTO dto = new PaymentExecutionRequestDTO();
        dto.setTransactionRef(tx.getTransactionRef());
        dto.setActorIdentifier(actorIdentifier);
        dto.setPaymentMode(tx.getPaymentMode());
        dto.setAmountInSoles(amountInSoles);

        log.info("Publicando ejecución de pago: {}", dto);
        kafkaTemplate.send("payment-execution-request", dto);
    }
}
