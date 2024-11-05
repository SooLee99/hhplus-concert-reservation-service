package org.example.hhplusconcertreservationservice.reservations.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hhplusconcertreservationservice.concerts.domain.Concert;
import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertRepository;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertScheduleRepository;
import org.example.hhplusconcertreservationservice.global.exception.ErrorResponse;
import org.example.hhplusconcertreservationservice.reservations.application.dto.request.SeatInfoRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.TokenRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.CreateReservationResponse;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.domain.SeatType;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatTypeRepository;
import org.example.hhplusconcertreservationservice.users.application.common.TokenGenerator;
import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.example.hhplusconcertreservationservice.users.interfaces.dto.response.InterfacesQueueResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReservationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatTypeRepository seatTypeRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private TokenGenerator tokenGenerator;

    private final String BASE_URL = "/api/v1/reservations";
    private final String QUEUE_URL = "/api/v1/queue";
    private final String BALANCE_URL = "/api/v1/balance";

    private Long userId;
    private String validToken;
    private Long seatId;

    private String userAuthToken;  // 사용자 인증 토큰을 저장할 변수 추가

    @BeforeEach
    void setUp() throws Exception {
        // 데이터베이스 초기화
        seatRepository.deleteAll();
        seatTypeRepository.deleteAll();
        concertRepository.deleteAll();
        concertScheduleRepository.deleteAll();
        queueRepository.deleteAll();

        // 테스트용 사용자 ID 설정
        userId = 1L;  // data.sql에서 삽입한 사용자 ID와 동일하게 설정

        // 사용자 인증 토큰 생성
        userAuthToken = tokenGenerator.generateToken(userId);

        // 토큰 발급 (대기열 토큰)
        validToken = issueToken(userId);

        // 잔액 충전
        chargeUserBalance(userId, BigDecimal.valueOf(100000));

        // 테스트용 좌석 ID 설정
        seatId = 1L;

        // 좌석 및 공연 정보 설정
        setupTestSeatAndConcert(seatId);
    }

    private void chargeUserBalance(Long userId, BigDecimal amount) throws Exception {
        ChargeBalanceRequest chargeRequest = new ChargeBalanceRequest(userId, amount);
        HttpHeaders headers = new HttpHeaders();

        // 사용자 인증 토큰 사용
        headers.setBearerAuth(userAuthToken);
        HttpEntity<ChargeBalanceRequest> entity = new HttpEntity<>(chargeRequest, headers);

        ResponseEntity<String> balanceResponse = restTemplate.postForEntity(
                BALANCE_URL + "/charge",
                entity,
                String.class
        );

        // 상태 코드와 응답 바디 출력
        System.out.println("Status Code: " + balanceResponse.getStatusCode());
        System.out.println("Response Body: " + balanceResponse.getBody());

        // 상태 코드 확인
        assertEquals(HttpStatus.OK, balanceResponse.getStatusCode(), "잔액 충전 요청이 실패하였습니다.");

        // 응답 바디를 UserBalanceResponse로 역직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        UserBalanceResponse userBalanceResponse = objectMapper.readValue(balanceResponse.getBody(), UserBalanceResponse.class);
        assertNotNull(userBalanceResponse);
    }

    private String issueToken(Long userId) {
        // 인증 헤더 없이 요청을 보냅니다.
        ResponseEntity<InterfacesQueueResponse> tokenResponse = restTemplate.postForEntity(
                QUEUE_URL + "/issue-token?userId=" + userId,
                null,
                InterfacesQueueResponse.class
        );

        // 상태 코드와 응답 바디 출력
        System.out.println("Status Code: " + tokenResponse.getStatusCode());
        System.out.println("Response Body: " + tokenResponse.getBody());

        assertEquals(HttpStatus.OK, tokenResponse.getStatusCode(), "토큰 발급 요청이 실패하였습니다.");
        InterfacesQueueResponse tokenBody = tokenResponse.getBody();
        assertNotNull(tokenBody, "토큰 응답이 null입니다.");
        return tokenBody.getQueueToken();
    }

    private void setupTestSeatAndConcert(Long seatId) {
        // 공연 정보 설정
        Concert concert = Concert.builder()
                .concertId(1L)
                .title("Spring Concert")
                .location("Seoul Arts Center")
                .posterUrl("http://example.com/concert.jpg")
                .rating("4.8")
                .build();

        concertRepository.save(concert);

        // 공연 스케줄 정보 설정
        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .scheduleId(1L)
                .concertId(concert.getConcertId())
                .date(LocalDateTime.now().plusDays(1))
                .duration(120)
                .ticketStartTime(LocalDateTime.now().minusHours(1))
                .ticketEndTime(LocalDateTime.now().plusHours(2))
                .build();

        concertScheduleRepository.save(concertSchedule);

        // 좌석 타입 정보 설정
        SeatType seatType = SeatType.builder()
                .seatTypeId(1L)
                .concertId(concert.getConcertId())
                .seatTypeName("VIP")
                .price(BigDecimal.valueOf(50000))
                .build();

        seatTypeRepository.save(seatType);

        // 좌석 정보 설정
        Seat seat = Seat.builder()
                .scheduleId(concertSchedule.getScheduleId())
                .seatTypeId(seatType.getSeatTypeId())
                .seatNumber(10)
                .isReserved(false)
                .build();

        seatRepository.save(seat);
    }

    @Nested
    @DisplayName("예약 가능 날짜 조회 API 테스트")
    class GetAvailableDatesTest {

        @Test
        @DisplayName("유효한 토큰으로 예약 가능한 날짜 목록을 조회해야 한다")
        void getAvailableDates_ShouldReturnDates_WhenTokenIsValid() {
            // Given
            TokenRequest tokenRequest = new TokenRequest(validToken);

            // When
            ResponseEntity<List> response = restTemplate.postForEntity(
                    BASE_URL + "/available-dates",
                    tokenRequest,
                    List.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertFalse(response.getBody().isEmpty(), "예약 가능한 날짜 목록이 반환되어야 합니다.");
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 요청하면 에러를 반환해야 한다")
        void getAvailableDates_ShouldReturnError_WhenTokenIsInvalid() {
            // Given
            String invalidToken = "invalid_token";
            TokenRequest tokenRequest = new TokenRequest(invalidToken);

            // When
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/available-dates",
                    tokenRequest,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("TOKEN_EXPIRED", errorResponse.getErrorCode());
        }

        @Test
        @DisplayName("토큰이 없을 경우 에러를 반환해야 한다")
        void getAvailableDates_ShouldReturnError_WhenTokenIsMissing() {
            // Given
            TokenRequest tokenRequest = new TokenRequest(null);

            // When
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/available-dates",
                    tokenRequest,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("MISSING_TOKEN", errorResponse.getErrorCode());
        }
    }

    @Nested
    @DisplayName("날짜별 좌석 정보 조회 API 테스트")
    class GetSeatInfoByDateTest {

        @Test
        @DisplayName("유효한 토큰과 날짜로 좌석 정보를 조회해야 한다")
        void getSeatInfoByDate_ShouldReturnSeatInfo_WhenTokenAndDateAreValid() {
            // Given
            LocalDateTime date = LocalDateTime.now().plusDays(1);
            SeatInfoRequest request = new SeatInfoRequest(date, validToken);

            // When
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    BASE_URL + "/seats",
                    request,
                    Map.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Map<String, Object> body = response.getBody();
            assertNotNull(body);
            assertTrue(body.containsKey("seatInfoList"), "좌석 정보 리스트가 포함되어야 합니다.");
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 요청하면 에러를 반환해야 한다")
        void getSeatInfoByDate_ShouldReturnError_WhenTokenIsInvalid() {
            // Given
            String invalidToken = "invalid_token";
            LocalDateTime date = LocalDateTime.now().plusDays(1);
            SeatInfoRequest request = new SeatInfoRequest(date, invalidToken);

            // When
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/seats",
                    request,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("TOKEN_EXPIRED", errorResponse.getErrorCode());
        }

        @Test
        @DisplayName("토큰이 없을 경우 에러를 반환해야 한다")
        void getSeatInfoByDate_ShouldReturnError_WhenTokenIsMissing() {
            // Given
            LocalDateTime date = LocalDateTime.now().plusDays(1);
            SeatInfoRequest request = new SeatInfoRequest(date, null);

            // When
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/seats",
                    request,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("MISSING_TOKEN", errorResponse.getErrorCode());
        }

        @Test
        @DisplayName("유효하지 않은 날짜로 요청하면 에러를 반환해야 한다")
        void getSeatInfoByDate_ShouldReturnError_WhenDateIsInvalid() {
            // Given
            LocalDateTime invalidDate = LocalDateTime.now().minusDays(1);
            SeatInfoRequest request = new SeatInfoRequest(invalidDate, validToken);

            // When
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/seats",
                    request,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("INVALID_RESERVATION_TIME", errorResponse.getErrorCode());
        }
    }

    @Nested
    @DisplayName("좌석 예약 요청 처리 API 테스트")
    class ReserveSeatTest {

        @Test
        @DisplayName("유효한 요청으로 좌석 예약에 성공해야 한다")
        void reserveSeat_ShouldReserveSuccessfully_WhenRequestIsValid() throws Exception {
            // Given
            CreateReservationRequest request = new CreateReservationRequest(validToken, seatId);

            // When
            ResponseEntity<String> response = restTemplate.postForEntity(
                    BASE_URL + "/seats/reserve",
                    request,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            String responseBody = response.getBody();
            assertNotNull(responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            CreateReservationResponse body = objectMapper.readValue(responseBody, CreateReservationResponse.class);
            assertNotNull(body);
            assertEquals("PENDING", body.getReservationStatus());
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 요청하면 에러를 반환해야 한다")
        void reserveSeat_ShouldReturnError_WhenTokenIsInvalid() {
            // Given
            String invalidToken = "invalid_token";
            CreateReservationRequest request = new CreateReservationRequest(invalidToken, seatId);

            // When
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/seats/reserve",
                    request,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("TOKEN_EXPIRED", errorResponse.getErrorCode());
        }

        @Test
        @DisplayName("토큰이 없을 경우 에러를 반환해야 한다")
        void reserveSeat_ShouldReturnError_WhenTokenIsMissing() {
            // Given
            CreateReservationRequest request = new CreateReservationRequest(null, seatId);

            // When
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/seats/reserve",
                    request,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("MISSING_TOKEN", errorResponse.getErrorCode());
        }

        @Test
        @DisplayName("이미 예약된 좌석을 예약하려고 하면 에러를 반환해야 한다")
        void reserveSeat_ShouldReturnError_WhenSeatAlreadyReserved() throws Exception {
            // Given
            CreateReservationRequest request = new CreateReservationRequest(validToken, seatId);

            // 선행 예약
            ResponseEntity<String> firstResponse = restTemplate.postForEntity(
                    BASE_URL + "/seats/reserve",
                    request,
                    String.class
            );
            assertEquals(HttpStatus.OK, firstResponse.getStatusCode());

            // 동일한 좌석으로 다시 예약 시도
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/seats/reserve",
                    request,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("SEAT_ALREADY_RESERVED", errorResponse.getErrorCode());
        }

        @Test
        @DisplayName("유효하지 않은 좌석 ID로 요청하면 에러를 반환해야 한다")
        void reserveSeat_ShouldReturnError_WhenSeatIdIsInvalid() {
            // Given
            Long invalidSeatId = 9999L;
            CreateReservationRequest request = new CreateReservationRequest(validToken, invalidSeatId);

            // When
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/seats/reserve",
                    request,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("SEAT_NOT_FOUND", errorResponse.getErrorCode());
        }

        @Test
        @DisplayName("사용자가 예약 가능한 좌석 수를 초과하면 에러를 반환해야 한다")
        void reserveSeat_ShouldReturnError_WhenMaxSeatsExceeded() throws Exception {
            // Given
            Long seatId1 = seatId;
            Long seatId2 = seatId + 1;

            // 추가 좌석 생성
            setupTestSeatAndConcert(seatId2);

            CreateReservationRequest request1 = new CreateReservationRequest(validToken, seatId1);
            CreateReservationRequest request2 = new CreateReservationRequest(validToken, seatId2);

            // 최대 예약 수가 1개라고 가정하고 첫 번째 예약 수행
            ResponseEntity<String> firstResponse = restTemplate.postForEntity(
                    BASE_URL + "/seats/reserve",
                    request1,
                    String.class
            );
            assertEquals(HttpStatus.OK, firstResponse.getStatusCode());

            // 두 번째 예약 시도
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/seats/reserve",
                    request2,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("MAX_SEATS_EXCEEDED", errorResponse.getErrorCode());
        }

        @Test
        @DisplayName("필수 입력 값이 누락되면 에러를 반환해야 한다")
        void reserveSeat_ShouldReturnError_WhenRequiredFieldsMissing() {
            // Given
            CreateReservationRequest request = new CreateReservationRequest(null, null);

            // When
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/seats/reserve",
                    request,
                    ErrorResponse.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("MISSING_REQUIRED_FIELDS", errorResponse.getErrorCode());
        }
    }
}
