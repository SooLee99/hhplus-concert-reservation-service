package org.example.hhplusconcertreservationservice.users.application.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class QueueTokenInterceptor implements HandlerInterceptor {

    private final QueueService queueService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청 헤더에서 토큰을 가져옵니다.
        String token = request.getHeader("Authorization"); // Authorization 헤더에서 토큰을 가져오는 방식

        // 토큰이 없는 경우 예외 발생
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(ExceptionMessage.MISSING_TOKEN.getMessage());
        }

        // 토큰 유효성 검증
        queueService.validateQueueToken(token);

        // 유효한 토큰일 경우 요청을 계속 처리
        return true;
    }
}
