package com.bootcoin.event.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.bootcoin.op.dto.ConfirmTransactionRequest;
import com.bootcoin.dto.PaymentValidationRequestDTO;
import com.bootcoin.model.BootCoinTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentValidationPublisher {
	private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "payment-validation-request";

    public void publishValidationRequest(BootCoinTransaction tx, ConfirmTransactionRequest request) {

        PaymentValidationRequestDTO dto = PaymentValidationRequestDTO.builder()
            .transactionRef(tx.getTransactionRef())
            .actorIdentifier(request.getActorIdentifier()) //CAMBIO AQUÍ
            .paymentMode(tx.getPaymentMode())
            .transactionType(tx.getTransactionType())
            .amountInSoles(
                "BUY".equalsIgnoreCase(tx.getTransactionType())
                    ? tx.getAmountInSoles()
                    : tx.getAmountInBootCoins().multiply(tx.getExchangeRateAtRequest())
            )
            .accountNumber(request.getAccountNumber())
            .phoneNumber(request.getPhoneNumber())
            .build();

        log.info("Publishing validation request: {}", dto);
        kafkaTemplate.send(TOPIC, dto);
    }
}
