package org.example.best.config;

import org.redisson.api.RedissonClient;
import org.redisson.codec.Kryo5Codec;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedissonCacheConfig {

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {

        Map<String, CacheConfig> config = new HashMap<>();

        long oneHourInMs = Duration.ofHours(1).toMillis();
        CacheConfig defaultConfig = new CacheConfig(oneHourInMs, oneHourInMs);
        config.put("events", new CacheConfig(Duration.ofHours(2).toMillis(), Duration.ofHours(2).toMillis()));
        config.put("eventsList", new CacheConfig(Duration.ofMinutes(10).toMillis(), Duration.ofMinutes(10).toMillis()));
        config.put("bookings", defaultConfig);
        config.put("bookingsList", new CacheConfig(Duration.ofMinutes(5).toMillis(), Duration.ofMinutes(5).toMillis()));
        config.put("users", new CacheConfig(Duration.ofMinutes(30).toMillis(), Duration.ofMinutes(30).toMillis()));

        var codec = new Kryo5Codec();

        return new RedissonSpringCacheManager(redissonClient,
                config, codec);

    }
}
