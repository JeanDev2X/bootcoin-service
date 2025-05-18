package com.bootcoin.service;

public interface KafkaProducerService {
	void send(String topic, Object event);
}
