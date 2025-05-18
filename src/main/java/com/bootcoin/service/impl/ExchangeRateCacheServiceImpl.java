package com.bootcoin.service.impl;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import com.bootcoin.model.ExchangeRateCache;
import com.bootcoin.service.ExchangeRateCacheService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service																																																																																																																																																																																																																																																																																																																																			
@RequiredArgsConstructor
public class ExchangeRateCacheServiceImpl implements ExchangeRateCacheService{

	private final ReactiveRedisTemplate<String, Object> redisTemplate;
	private static final String KEY = "exchangeRate";
	
	@Override
	public Mono<Void> saveRate(ExchangeRateCache rate) {
		return redisTemplate.opsForValue().set(KEY, rate).then();
	}

	@Override
	public Mono<ExchangeRateCache> getRate() {
		return redisTemplate.opsForValue().get(KEY).cast(ExchangeRateCache.class);
	}

}
