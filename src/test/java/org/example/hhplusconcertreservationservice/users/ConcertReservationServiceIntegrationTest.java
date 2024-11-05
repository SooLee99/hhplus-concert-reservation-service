package org.example.hhplusconcertreservationservice.users;

import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.service.balance.UseBalanceService;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueManager;
import org.example.hhplusconcertreservationservice.users.application.service.queue.ServerLoadMonitor;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceRepository;
import org.junit.jupiter.api.*;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.example.hhplusconcertreservationservice.global.config.LockHandler;
import org.example.hhplusconcertreservationservice.global.config.TransactionHandler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
public class ConcertReservationServiceIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcertReservationServiceIntegrationTest.class);

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UseBalanceService useBalanceService;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private QueueManager queueManager;

    @Autowired
    private ServerLoadMonitor serverLoadMonitor;

    @Autowired
    private LockHandler lockHandler;

    @Autowired
    private TransactionHandler transactionHandler;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TestDataInitializer testDataInitializer;

    private static final Long TEST_USER_ID = 1L;
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(5000);
    private static final int USERS_COUNT = 10000;

    private static final String QUEUE_TOPIC = "queue-topic";
    private static final String RESERVATION_TOPIC = "reservation-topic";
    private ExecutorService executorService;

    @BeforeEach
    public void setup() {
        // 스레드 풀 크기 조정
        int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        // 사용자 잔액 초기 설정
        testDataInitializer.initializeUserBalance(TEST_USER_ID, INITIAL_BALANCE);
        // 사용자 잔액 초기 설정
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            userBalanceRepository.findByUserId(TEST_USER_ID)
                    .orElseGet(() -> userBalanceRepository.save(new UserBalance(TEST_USER_ID, INITIAL_BALANCE)));
            return null;
        });
    }

    @AfterEach
    public void tearDown() {
        executorService.shutdown();
    }

    @Test
    @DisplayName("한명의 사용자가 동시 잔액 충전 및 차감 테스트 - Redis 락 적용")
    public void testConcurrentBalanceChargeAndDeductionWithRedisLock() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);  // 두 개의 작업: 충전과 차감

        Runnable chargeBalanceTask = () -> {
            String lockKey = "balance_lock_" + TEST_USER_ID;
            try {
                lockHandler.runOnLock(lockKey, () -> {
                    return transactionHandler.runOnWriteTransaction(() -> {
                        ChargeBalanceRequest chargeRequest = new ChargeBalanceRequest(TEST_USER_ID, BigDecimal.valueOf(10));
                        try {
                            LOGGER.info("스레드 {}: 잔액 충전을 시도합니다.", Thread.currentThread().getName());
                            useBalanceService.chargeBalance(chargeRequest);
                            LOGGER.info("스레드 {}: 잔액 충전에 성공했습니다.", Thread.currentThread().getName());
                        } catch (Exception e) {
                            LOGGER.error("스레드 {}: 잔액 충전 실패 - {}", Thread.currentThread().getName(), e.getMessage());
                        }
                        return null;
                    });
                });
            } finally {
                latch.countDown();
            }
        };

        Runnable deductBalanceTask = () -> {
            String lockKey = "balance_lock_" + TEST_USER_ID;
            try {
                lockHandler.runOnLock(lockKey, () -> {
                    return transactionHandler.runOnWriteTransaction(() -> {
                        ChargeBalanceRequest deductRequest = new ChargeBalanceRequest(TEST_USER_ID, BigDecimal.valueOf(5));
                        try {
                            LOGGER.info("스레드 {}: 잔액 차감을 시도합니다.", Thread.currentThread().getName());
                            useBalanceService.useBalance(deductRequest);
                            LOGGER.info("스레드 {}: 잔액 차감에 성공했습니다.", Thread.currentThread().getName());
                        } catch (Exception e) {
                            LOGGER.error("스레드 {}: 잔액 차감 실패 - {}", Thread.currentThread().getName(), e.getMessage());
                        }
                        return null;
                    });
                });
            } finally {
                latch.countDown();
            }
        };

        // 동시에 잔액 충전 및 차감 작업 시작
        executorService.submit(chargeBalanceTask);
        executorService.submit(deductBalanceTask);

        latch.await();

        // 충전 및 차감 후 최종 잔액 확인
        UserBalance finalBalance = userBalanceRepository.findById(TEST_USER_ID).orElseThrow();
        LOGGER.info("최종 잔액: {}", finalBalance.getBalance());
        BigDecimal expectedBalance = INITIAL_BALANCE.add(BigDecimal.valueOf(5));  // 10 충전 - 5 차감

        // compareTo()를 사용하여 값 비교
        assertEquals(0, expectedBalance.compareTo(finalBalance.getBalance()), "최종 잔액이 예상 값과 일치해야 합니다.");
    }

    @Test
    @Transactional
    @DisplayName("50개의 좌석에 대해 각 좌석당 만 명의 사용자가 동시에 예약 시도 - Redis 분산 락 적용")
    public void testConcurrentSeatReservationsForMultipleSeatsWithRedisLock() throws InterruptedException {
        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        final int seatCount = 50;
        final int usersPerSeat = 10000;
        AtomicInteger currentSuccess = new AtomicInteger();
        AtomicInteger currentFailure = new AtomicInteger();
        // 좌석 초기화
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= seatCount; i++) {
            Seat seat = new Seat(1L, 1L, i, false);  // Builder 사용 없이 직접 객체 생성
            seats.add(seat);
        }
        seatRepository.saveAll(seats); // 좌석 리스트를 저장

        CountDownLatch latch = new CountDownLatch(seatCount * usersPerSeat);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger processedCount = new AtomicInteger(0);

        for (int i = 0; i < usersPerSeat; i++) {
            final Long userId = (long) (i + 1);  // 유일한 사용자 ID 생성
            executorService.submit(() -> {
                for (Seat seat : seats) {
                    String lockKey = "reservation_lock_" + seat.getSeatNumber();

                    // 좌석별 고유 락을 사용하여 예약 시도
                    lockHandler.runOnLock(lockKey, () -> transactionHandler.runOnWriteTransaction(() -> {
                        LOGGER.info("사용자 {}가 좌석 {} 예약에 대한 락을 획득", userId, seat.getSeatNumber());

                        // 현재 좌석 상태 확인 후 예약 시도
                        Optional<Seat> seatOptional = seatRepository.findById(seat.getSeatId());
                        if (!seatOptional.get().isReserved()) {
                            Seat dbSeat = seatOptional.get();
                            dbSeat.reserve();
                            seatRepository.save(dbSeat);
                            currentSuccess.set(successCount.incrementAndGet());
                            LOGGER.info("사용자 {}가 좌석 {} 예약 성공 (현재 성공 수: {})", userId, seat.getSeatNumber(), currentSuccess);
                        } else {
                            currentFailure.set(failureCount.incrementAndGet());
                            LOGGER.info("사용자 {}가 좌석 {} 예약 실패 - 이미 예약됨 (현재 실패 수: {})", userId, seat.getSeatNumber(), currentFailure);
                        }
                        return null;
                    }));

                    int currentProcessed = processedCount.incrementAndGet();
                    if (currentProcessed % 100 == 0) {
                        LOGGER.info("처리된 예약 시도 수: {}", currentProcessed);
                    }
                    latch.countDown();
                }
            });
        }

        latch.await();
        LOGGER.info("성공한 좌석 예약 요청 수: {}", currentSuccess.get());
        LOGGER.info("실패한 좌석 예약 요청 수: {}", currentFailure.get());
        LOGGER.info("성공한 좌석 예약 요청 수: {}번 / 실패한 좌석 예약 요청 수: {}번 / 총 좌석 수: {}개 / 예약된 좌석 수: {}개",
                currentSuccess.get(), currentFailure.get(), seatCount, seatRepository.countByIsReservedTrue());
        logPerformance("좌석 예약 동시 테스트", startTime, startMemory);
        assertEquals(seatCount, currentSuccess.get(), "각 좌석당 하나의 성공적인 예약만 있어야 합니다.");
        assertEquals(seatCount * usersPerSeat - seatCount, currentFailure.get(), "실패한 예약 시도가 올바르게 처리되어야 합니다.");
    }

    @Test
    @DisplayName("여러 사용자가 동시에 큐에 진입할 때 FIFO 순서 유지 테스트 - Redis Sorted Set 적용")
    public void testFIFOQueueEntryOrderWithRedisSortedSet() throws InterruptedException {
        String queueKey = "userQueue";
        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        RedissonClient redissonClient = lockHandler.getRedissonClient();
        RScoredSortedSet<Long> redisSortedSet = redissonClient.getScoredSortedSet(queueKey);

        // 기존 큐 초기화
        redisSortedSet.clear();

        CountDownLatch latch = new CountDownLatch(USERS_COUNT);
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < USERS_COUNT; i++) {
            userIds.add(TEST_USER_ID + i);
        }

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger processedCount = new AtomicInteger(0);

        // 시퀀스 번호를 메인 스레드에서 할당
        for (int i = 0; i < userIds.size(); i++) {
            final long sequenceNumber = i + 1; // 시퀀스 번호는 1부터 시작
            final Long userId = userIds.get(i);
            executorService.submit(() -> {
                try {
                    LOGGER.info("사용자 {}가 큐에 진입 시도 중입니다.", userId);
                    // Redis Sorted Set에 사용자 ID를 시퀀스 번호를 스코어로 하여 추가합니다.
                    boolean added = redisSortedSet.add(sequenceNumber, userId);

                    if (added) {
                        int currentSuccess = successCount.incrementAndGet();
                        LOGGER.info("사용자 {}가 큐에 성공적으로 진입했습니다. 현재 성공한 진입 수: {}", userId, currentSuccess);

                        if (currentSuccess % 100 == 0) {
                            LOGGER.info("현재까지 성공적으로 큐에 추가된 사용자 수: {}", currentSuccess);
                        }
                    } else {
                        int currentFailure = failureCount.incrementAndGet();
                        LOGGER.warn("사용자 {}가 큐 진입에 실패했습니다. 현재 실패한 진입 수: {}", userId, currentFailure);

                        if (currentFailure % 10 == 0) {
                            LOGGER.info("현재까지 큐 진입에 실패한 사용자 수: {}", currentFailure);
                        }
                    }
                } catch (Exception e) {
                    int currentFailure = failureCount.incrementAndGet();
                    LOGGER.error("사용자 {}의 큐 진입 중 예외 발생: {}", userId, e.getMessage());
                } finally {
                    int currentProcessed = processedCount.incrementAndGet();
                    if (currentProcessed % 100 == 0) {
                        LOGGER.info("처리된 큐 진입 요청 수: {}", currentProcessed);
                    }
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Redis Sorted Set에서 모든 사용자 ID를 순서대로 가져옵니다.
        Collection<Long> queuedUserIds = redisSortedSet.valueRange(0, -1); // 인덱스 기반 조회

        LOGGER.info("최종적으로 성공한 큐 진입 요청 수: {}개 / 실패한 큐 진입 요청 수: {}개", successCount.get(), failureCount.get());
        logPerformance("FIFO 큐 진입 테스트", startTime, startMemory);
        assertEquals(USERS_COUNT, queuedUserIds.size(), "큐에 저장된 사용자 수가 일치해야 합니다.");
        assertTrue(isFIFO(new ArrayList<>(queuedUserIds), userIds), "큐 순서가 FIFO로 유지되어야 합니다.");

        // 테스트 완료 후 Redis 큐를 정리합니다.
        redisSortedSet.delete();
    }

    private boolean isFIFO(List<Long> queuedUserIds, List<Long> originalUserIds) {
        // 원본 사용자 ID 리스트와 Redis 큐의 사용자 ID 리스트를 비교하여 순서가 동일한지 확인합니다.
        return queuedUserIds.equals(originalUserIds);
    }

    private void logPerformance(String testName, long startTime, long startMemory) {
        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long duration = (endTime - startTime) / 1_000_000;
        long memoryUsed = (endMemory - startMemory) / (1024 * 1024);

        LOGGER.info("{} - 소요 시간: {} ms", testName, duration);
        LOGGER.info("{} - 메모리 사용량: {} MB", testName, memoryUsed);
    }
}

