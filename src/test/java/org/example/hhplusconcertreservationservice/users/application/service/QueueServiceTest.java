package org.example.hhplusconcertreservationservice.users.application.service;

import org.example.hhplusconcertreservationservice.users.application.common.QueuePositionCalculator;
import org.example.hhplusconcertreservationservice.users.application.common.TokenGenerator;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.exception.*;
import org.example.hhplusconcertreservationservice.users.application.service.queue.*;
import org.example.hhplusconcertreservationservice.users.application.usecase.IssueQueueTokenUseCase;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @InjectMocks
    private QueueManager queueManager;

    @Mock
    private QueueProcessor queueProcessor;

    @InjectMocks
    private QueueService queueService;

    @Mock
    private QueueValidationService queueValidationService;



    @Mock
    private ServerLoadMonitor serverLoadMonitor;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private QueuePositionCalculator queuePositionCalculator;

    @Mock
    private IssueQueueTokenUseCase issueQueueTokenUseCase;

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private Clock clock;

    private static final int MAX_CAPACITY = 30;

    @BeforeEach
    void setUp() {
        // 고정된 시간 설정
        Instant fixedInstant = Instant.parse("2024-10-17T10:00:00Z");
        ZoneId zoneId = ZoneId.of("UTC");

        // Clock의 instant()와 getZone() 모킹 설정 (lenient()로 불필요한 경고 방지)
        lenient().when(clock.instant()).thenReturn(fixedInstant);
        lenient().when(clock.getZone()).thenReturn(zoneId);
    }

    @DisplayName("유저에게 대기열 토큰을 정상적으로 발급하는 테스트")
    @Test
    void givenUserId_whenGenerateQueueToken_thenTokenIsIssued() {
        // given
        Long userId = 1L;
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.empty());
        when(tokenGenerator.generateToken(userId)).thenReturn("token_123");
        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(queuePositionCalculator.calculatePosition(userId)).thenReturn(1);
        when(queuePositionCalculator.calculateEstimatedWaitTime(userId)).thenReturn(Duration.ofMinutes(5));

        // when
        ApplicationQueueResponse response = queueManager.issueQueueToken(userId, MAX_CAPACITY);

        // then
        assertNotNull(response);
        assertEquals("token_123", response.getQueueToken());
    }

    @DisplayName("이미 발급된 토큰이 있을 때 새로운 토큰 발급 시도 시 예외 발생 테스트")
    @Test
    void givenExistingToken_whenIssueNewToken_thenThrowException() {
        // given
        Long userId = 1L;
        Queue existingQueue = Queue.builder()
                .userId(userId)
                .queueToken("token123")
                .status(QueueStatus.WAITING)
                .expirationTime(LocalDateTime.now(clock).plusMinutes(5))
                .build();
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.of(existingQueue));

        // when, then
        ActiveTokenExistsException thrown = assertThrows(ActiveTokenExistsException.class, () -> queueManager.issueQueueToken(userId, MAX_CAPACITY));
        assertEquals(ExceptionMessage.ACTIVE_TOKEN_EXISTS.getMessage(), thrown.getMessage());
    }

    @DisplayName("유저의 현재 대기열 위치를 조회하는 테스트")
    @Test
    void givenUserWithQueue_whenGetQueuePosition_thenReturnCorrectPosition() {
        // given
        Long userId = 1L;
        Queue queue = Queue.builder()
                .userId(userId)
                .queueToken("token123")
                .queuePosition(1)
                .status(QueueStatus.WAITING)
                .expirationTime(LocalDateTime.now(clock).plusMinutes(10))
                .build();
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.of(queue));
        when(queuePositionCalculator.calculatePosition(userId)).thenReturn(1);
        when(queuePositionCalculator.calculateEstimatedWaitTime(userId)).thenReturn(Duration.ofMinutes(5));

        // when
        ApplicationQueueResponse response = queueService.getCurrentQueueStatus(userId);

        // then
        assertNotNull(response);
        assertEquals(1, response.getQueuePosition());
    }

    @DisplayName("유저가 대기열에서 자발적으로 이탈하는 테스트")
    @Test
    void givenUserInQueue_whenLeaveQueue_thenUserIsRemovedFromQueue() {
        // given
        Long userId = 1L;
        doNothing().when(queueRepository).deleteByUserId(userId);

        // when
        queueManager.removeUserFromQueue(userId);

        // then
        verify(queueRepository, times(1)).deleteByUserId(userId);
    }

    @DisplayName("만료 시간이 지난 대기열이 자동으로 삭제되는 테스트")
    @Test
    void givenExpiredQueues_whenRemoveExpiredQueues_thenOnlyValidQueuesRemain() {
        // given
        LocalDateTime now = LocalDateTime.now(clock);
        Queue expiredQueue = Queue.builder()
                .userId(1L)
                .queueToken("expiredToken")
                .expirationTime(now.minusMinutes(5))
                .status(QueueStatus.WAITING)
                .build();
        Queue validQueue = Queue.builder()
                .userId(2L)
                .queueToken("validToken")
                .expirationTime(now.plusMinutes(10))
                .status(QueueStatus.WAITING)
                .build();
        when(queueRepository.findAll()).thenReturn(List.of(expiredQueue, validQueue));
        when(queueRepository.deleteAllByExpirationTimeBefore(any(LocalDateTime.class))).thenReturn(1);

        // when
        queueService.removeExpiredQueues();

        // then
        verify(queueRepository, times(1)).deleteAllByExpirationTimeBefore(any(LocalDateTime.class));
    }

    @DisplayName("만료된 토큰으로 대기열 조회 시 예외 발생 테스트")
    @Test
    void givenExpiredToken_whenGetQueueStatus_thenThrowTokenExpiredException() {
        // given
        Long userId = 1L;
        LocalDateTime expiredTime = LocalDateTime.now(clock).minusMinutes(10);
        Queue expiredQueue = Queue.builder()
                .userId(userId)
                .queueToken("expiredToken")
                .expirationTime(expiredTime)
                .status(QueueStatus.WAITING)
                .build();
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.of(expiredQueue));

        // when, then
        TokenExpiredException thrown = assertThrows(TokenExpiredException.class, () -> queueService.getCurrentQueueStatus(userId));
        assertEquals(ExceptionMessage.TOKEN_EXPIRED.getMessage(), thrown.getMessage());
    }

    @DisplayName("대기열 생성 실패 시 예외 발생 테스트")
    @Test
    void givenRepositorySaveFailure_whenIssueQueueToken_thenThrowQueueCreationFailedException() {
        // given
        Long userId = 1L;
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.empty());
        when(queueRepository.save(any(Queue.class))).thenThrow(new RuntimeException("Database error"));

        // when, then
        QueueCreationFailedException thrown = assertThrows(QueueCreationFailedException.class, () -> queueManager.issueQueueToken(userId, MAX_CAPACITY));
        assertEquals(ExceptionMessage.QUEUE_CREATION_FAILED.getMessage(), thrown.getMessage());
    }

    @DisplayName("대기열 정보가 존재하지 않을 때 예외 발생 테스트")
    @Test
    void givenNoQueueEntry_whenGetQueueStatus_thenThrowQueueEntryNotFoundException() {
        // given
        Long userId = 1L;
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.empty());

        // when, then
        QueueEntryNotFoundException thrown = assertThrows(QueueEntryNotFoundException.class, () -> queueService.getCurrentQueueStatus(userId));
        assertEquals(ExceptionMessage.QUEUE_ENTRY_NOT_FOUND.getMessage(), thrown.getMessage());
    }

    @DisplayName("여러 사용자가 대기열에 추가될 때 대기 순서와 시간이 올바르게 업데이트되는지 테스트")
    @Test
    void givenMultipleUsers_whenIssueQueueToken_thenPositionsAndWaitTimesAreUpdated() {
        // given
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long userId3 = 3L;
        LocalDateTime now = LocalDateTime.now(clock);
        Queue queue1 = Queue.builder()
                .userId(userId1)
                .queueToken("token1")
                .queuePosition(1)
                .status(QueueStatus.WAITING)
                .expirationTime(now.plusMinutes(5))
                .build();
        Queue queue2 = Queue.builder()
                .userId(userId2)
                .queueToken("token2")
                .queuePosition(2)
                .status(QueueStatus.TOKEN_ISSUED)
                .expirationTime(now.plusMinutes(5))
                .build();
        when(queueRepository.findAll()).thenReturn(List.of(queue1, queue2));
        when(queueRepository.findActiveQueueByUserId(userId3)).thenReturn(Optional.empty());
        when(tokenGenerator.generateToken(userId3)).thenReturn("token3");
        when(queuePositionCalculator.calculatePosition(userId3)).thenReturn(3);
        when(queuePositionCalculator.calculateEstimatedWaitTime(userId3)).thenReturn(Duration.ofMinutes(10));
        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ApplicationQueueResponse response = queueManager.issueQueueToken(userId3, MAX_CAPACITY);
        System.out.println("Response: " + response);

        // then
        assertNotNull(response);
        assertEquals(3, response.getQueuePosition());
        assertEquals("token3", response.getQueueToken());
    }
}