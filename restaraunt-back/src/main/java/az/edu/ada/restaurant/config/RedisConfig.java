package az.edu.ada.restaurant.config;

import az.edu.ada.restaurant.dto.OrderTrackingDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory cf) {
        RedisTemplate<String,Object> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(cf);
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(jacksonSerializer());  // reuse the same jacksonSerializer() you defined
        tpl.setHashKeySerializer(new StringRedisSerializer());
        tpl.setHashValueSerializer(jacksonSerializer());
        tpl.afterPropertiesSet();
        return tpl;
    }


    private RedisSerializer<Object> jacksonSerializer() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );
        Jackson2JsonRedisSerializer<Object> ser = new Jackson2JsonRedisSerializer<>(Object.class);
        ser.setObjectMapper(mapper);
        return ser;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // serializer bound to your DTO:
        Jackson2JsonRedisSerializer<OrderTrackingDto> dtoSer =
                new Jackson2JsonRedisSerializer<>(OrderTrackingDto.class);
        // register JSR-310 module so Instant works:
        dtoSer.setObjectMapper(
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        );

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(dtoSer)
                );
    }

    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory cf) {
        return RedisCacheManager.builder(cf)
                .cacheDefaults(cacheConfiguration())
                .build();
    }
}
