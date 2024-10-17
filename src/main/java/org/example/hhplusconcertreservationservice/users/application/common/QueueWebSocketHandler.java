package org.example.hhplusconcertreservationservice.users.application.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class QueueWebSocketHandler extends TextWebSocketHandler {

    private final QueueRepository queueRepository;
    private final QueuePositionCalculator queuePositionCalculator;
    private final ObjectMapper objectMapper; // Object to JSON 변환용
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Long, ScheduledExecutorService> userSchedulers = new ConcurrentHashMap<>();

    public QueueWebSocketHandler(QueueRepository queueRepository, QueuePositionCalculator queuePositionCalculator, ObjectMapper objectMapper) {
        this.queueRepository = queueRepository;
        this.queuePositionCalculator = queuePositionCalculator;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = extractUserId(session); // WebSocket 세션에서 userId 추출 로직 추가
        startSendingQueueUpdates(session, userId); // 사용자에게 대기열 업데이트 전송 시작
    }

    private void startSendingQueueUpdates(WebSocketSession session, Long userId) {
        // 사용자별 스케줄러 생성
        ScheduledExecutorService userScheduler = Executors.newScheduledThreadPool(1);
        userSchedulers.put(userId, userScheduler);

        // 5초마다 사용자 위치와 예상 대기 시간을 전송
        userScheduler.scheduleAtFixedRate(() -> {
            try {
                int position = queuePositionCalculator.calculatePosition(userId);
                Duration estimatedWaitTime = queuePositionCalculator.calculateEstimatedWaitTime(userId);

                // Queue 정보 객체 생성
                QueueStatusUpdate update = new QueueStatusUpdate(position, estimatedWaitTime.toMinutes());

                // 객체를 JSON으로 변환하여 전송
                String jsonMessage = objectMapper.writeValueAsString(update);

                session.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS); // 5초마다 실행
    }

    private Long extractUserId(WebSocketSession session) {
        // URI에서 userId를 추출하는 로직
        String uri = session.getUri().toString(); // URI 가져오기
        String userIdStr = uri.substring(uri.lastIndexOf("/") + 1); // 마지막 '/' 이후의 부분
        return Long.parseLong(userIdStr); // 문자열을 Long으로 변환
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 클라이언트에서 수신한 메시지 처리 로직 (필요 시 구현)
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = extractUserId(session);
        ScheduledExecutorService userScheduler = userSchedulers.remove(userId);
        if (userScheduler != null) {
            userScheduler.shutdown();
        }
        session.close(); // WebSocket 세션 종료
    }

    // Queue 상태를 JSON으로 변환하기 위한 클래스
    static class QueueStatusUpdate {
        private final int position;
        private final long estimatedWaitTime;

        public QueueStatusUpdate(int position, long estimatedWaitTime) {
            this.position = position;
            this.estimatedWaitTime = estimatedWaitTime;
        }

        public int getPosition() {
            return position;
        }

        public long getEstimatedWaitTime() {
            return estimatedWaitTime;
        }
    }
}
