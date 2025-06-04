package com.bootcoin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bootcoin.dto.ConfirmTransactionRequest;
import com.bootcoin.model.BootCoinTransaction;
import com.bootcoin.model.BootCoinWallet;
import com.bootcoin.model.ExchangeRate;
import com.bootcoin.service.BootCoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/bootcoin")
@RequiredArgsConstructor
public class BootCoinController {

	private final BootCoinService bootCoinService;
	
	
	//REQ1
	@PostMapping("/exchange-rate")//tipo de cambio/tasa
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ExchangeRate> updateRate(@RequestBody ExchangeRate rate) {
		return bootCoinService.updateExchangeRate(rate);
	}
	//REQ1
	@GetMapping("/exchange-rate")//tipo de cambio
    public Mono<ExchangeRate> getRate() {
        return bootCoinService.getExchangeRate();
    }
	//REQ2
	@PostMapping("/wallets")//billetera
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BootCoinWallet> createWallet(@RequestBody BootCoinWallet wallet) {
        return bootCoinService.createWallet(wallet);
    }
	//REQ2
	@GetMapping("/wallets/{phoneNumber}")//billeteraxNumero
    public Mono<BootCoinWallet> getWallet(@PathVariable String phoneNumber) {
        return bootCoinService.getWalletByPhoneNumber(phoneNumber);
    }
	//REQ3
	/**
     * Solicita una transacción de BootCoin (BUY o SELL).
     * El body debe incluir el campo transactionType con valor "BUY" o "SELL".
     */
	 @PostMapping("/requestTransaction")
    public Mono<ResponseEntity<BootCoinTransaction>> requestTransaction(
            @RequestBody BootCoinTransaction transactionRequest) {
	 if ("BUY".equalsIgnoreCase(transactionRequest.getTransactionType())) {
	        log.info("Recibiendo solicitud de COMPRA BootCoin: soles={}, phone={}",
	                 transactionRequest.getAmountInSoles(), transactionRequest.getBuyerPhoneNumber());
	    } else if ("SELL".equalsIgnoreCase(transactionRequest.getTransactionType())) {
	        log.info("Recibiendo solicitud de VENTA BootCoin: bootcoins={}, phone={}",
	                 transactionRequest.getAmountInBootCoins(), transactionRequest.getSellerPhoneNumber());
	    }

	    return bootCoinService.requestTransaction(transactionRequest)
	            .map(savedTx -> {
	                log.info("Transacción solicitada registrada: id={}, tipo={}",
	                         savedTx.getId(), savedTx.getTransactionType());
	                return ResponseEntity.ok(savedTx);
	            });
    }
	
	@GetMapping("/transactions/pending")
    public Flux<BootCoinTransaction> getPendingTransactions() {
        return bootCoinService.getPendingTransactions();
    }
	
	@PatchMapping("/transactions/confirm")//Aceptar transaccion
	public Mono<BootCoinTransaction> confirmTransaction(@RequestBody ConfirmTransactionRequest request) {
		return bootCoinService.confirmTransaction(request);
    }
	
}
