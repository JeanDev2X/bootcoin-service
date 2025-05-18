package com.bootcoin.service.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bootcoin.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService{

	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	@Override
	public void send(String topic, Object event) {
		kafkaTemplate.send(topic, event);
	}

}
