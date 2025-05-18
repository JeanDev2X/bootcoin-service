package com.bootcoin.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
	// asegurarte de que los tópicos existan al iniciar
    @Bean
    public NewTopic bootcoinTransactionEventsTopic() {
        return new NewTopic("bootcoin-transaction-events", 1, (short) 1);
    }
}
