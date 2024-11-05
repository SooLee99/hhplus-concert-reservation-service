package org.example.hhplusconcertreservationservice.global.config;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.global.interceptor.QueueTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final QueueTokenInterceptor queueTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 특정 API 경로에만 Interceptor 적용
        registry.addInterceptor(queueTokenInterceptor)
                .addPathPatterns("/api/v1/balance/charge", "/api/v1/balance/use", "/api/v1/reservations/available-dates",
                        "/api/v1/reservations/seats/reserve", "/api/v1/reservations/seats", "/api/v1/payments/process");
    }
}
