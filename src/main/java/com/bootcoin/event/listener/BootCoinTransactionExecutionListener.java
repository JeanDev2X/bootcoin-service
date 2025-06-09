package com.bootcoin.event.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bootcoin.dto.*;
import com.bootcoin.repository.BootCoinTransactionRepository;
import com.bootcoin.repository.BootCoinWalletRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootCoinTransactionExecutionListener {
	
	private final BootCoinTransactionRepository transactionRepository;
    private final BootCoinWalletRepository walletRepository;
    
    @KafkaListener(
        topics = "payment-execution-response",
        groupId = "bootcoin-service",
        containerFactory = "paymentExecutionKafkaListenerContainerFactory"
    )
    public void handleExecutionResponse(PaymentExecutionResponseDTO response) {
        log.info("Recibido payment-execution-response: {}", response);

        transactionRepository.findByTransactionRef(response.getTransactionRef())
            .flatMap(tx -> {
                if (!response.isExecuted()) {
                    log.warn("Ejecución fallida para transacción {}: {}", tx.getTransactionRef(), response.getMessage());
                    tx.setStatus("FAILED");
                    return transactionRepository.save(tx);
                }

                log.info("Ejecución de pago completada para transacción {}", tx.getTransactionRef());
                tx.setStatus("COMPLETED");
                return transactionRepository.save(tx);
            })
            .doOnSuccess(tx -> log.info("Transacción actualizada tras ejecución: {}", tx.getTransactionRef()))
            .doOnError(e -> log.error("Error al manejar payment-execution-response: {}", e.getMessage()))
            .subscribe();
    }
    
}
