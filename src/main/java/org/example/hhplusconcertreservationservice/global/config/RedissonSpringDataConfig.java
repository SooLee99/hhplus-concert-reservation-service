package org.example.hhplusconcertreservationservice.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonSpringDataConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:mySecurePassword}")
    private String password;

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        String address = "redis://" + host + ":" + port;
        System.out.println("Configuring Redisson with address: " + address);
        System.out.println("Redis Host: " + host);
        System.out.println("Redis Port: " + port);  // 포트 값 확인
        System.out.println("Redis Password: " + (password.isEmpty() ? "No Password" : password));

        Config config = new Config();
        config.useSingleServer()
                .setAddress(address)
                .setPassword(password.isEmpty() ? "mySecurePassword" : password);

        return Redisson.create(config);
    }
}
