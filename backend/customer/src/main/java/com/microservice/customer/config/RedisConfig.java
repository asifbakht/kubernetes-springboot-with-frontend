package com.microservice.customer.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

import static com.microservice.customer.utils.Constants.CACHE_CUSTOMER;

/**
 * This class resides redis configuration
 *
 * @author Asif Bakht
 * @since 2024
 */
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${cache.app.time-to-live:10}")
    private int customerTTL;
    @Value("${cache.default.time-to-live:5}")
    private int defaultTTL;

    /**
     * create default redis connection factory that will be used
     * within different cache manager
     *
     * @return {@link LettuceConnectionFactory}
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        final RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(configuration);
    }

    /**
     * configure different types of cache, here payment is cached
     * with payment time to live
     *
     * @return {@link RedisCacheManager}
     */
    @Bean
    public RedisCacheManager cacheManager() {
        final RedisCacheConfiguration cacheConfig = myDefaultCacheConfig(Duration.ofMinutes(defaultTTL))
                .disableCachingNullValues();
        return RedisCacheManager
                .builder(redisConnectionFactory())
                .cacheDefaults(cacheConfig)
                .withCacheConfiguration(
                        CACHE_CUSTOMER,
                        myDefaultCacheConfig(Duration.ofMinutes(customerTTL))
                )
                .build();
    }

    /**
     * create default cache configuration that will be used by
     * different cache manager
     *
     * @param duration {@link Duration} minutes
     * @return {@link RedisCacheConfiguration} configuration
     */
    private RedisCacheConfiguration myDefaultCacheConfig(final Duration duration) {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(duration)
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        ));
    }
}