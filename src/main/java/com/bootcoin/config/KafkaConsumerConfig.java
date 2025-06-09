package com.bootcoin.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.bootcoin.dto.PaymentExecutionResponseDTO;
import com.bootcoin.dto.PaymentValidationResponseDTO;

@Configuration
public class KafkaConsumerConfig {
	
	@Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

//	@Bean
//    public ConsumerFactory<String, BootCoinTransactionEventDTO> bootCoinTransactionEventConsumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bootcoin-service");
//
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
//
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
//
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, BootCoinTransactionEventDTO.class.getName());
//
//        return new DefaultKafkaConsumerFactory<>(props);
//    }
//	
//	@Bean
//    public ConcurrentKafkaListenerContainerFactory<String, BootCoinTransactionEventDTO> bootCoinTransactionEventKafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, BootCoinTransactionEventDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(bootCoinTransactionEventConsumerFactory());
//        return factory;
//    }
	
	@Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentValidationResponseDTO> paymentValidationResponseKafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentValidationResponseDTO> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, PaymentValidationResponseDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentValidationResponseDTO> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Ajusta según tu config
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bootcoin-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new JsonDeserializer<>(PaymentValidationResponseDTO.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentExecutionResponseDTO> paymentExecutionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentExecutionResponseDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(
                Map.of(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                    JsonDeserializer.TRUSTED_PACKAGES, "*"
                )
        ));
        return factory;
    }
	
}
