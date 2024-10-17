package org.example.hhplusconcertreservationservice.reservations.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hhplusconcertreservationservice.reservations.application.service.CreateReservationService;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetAvailableDatesUseCase;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetSeatInfoByDateUseCase;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.SeatStatus;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.SeatInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetAvailableDatesUseCase getAvailableDatesUseCase;

    @MockBean
    private GetSeatInfoByDateUseCase getSeatInfoByDateUseCase;

    @MockBean
    private CreateReservationService createReservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("예약 가능한 날짜 목록을 성공적으로 조회한다")
    void testGetAvailableDates_Success() throws Exception {
        // Given
        LocalDateTime date1 = LocalDateTime.of(2024, 10, 20, 19, 0);
        LocalDateTime date2 = LocalDateTime.of(2024, 10, 21, 19, 0);
        List<LocalDateTime> availableDates = Arrays.asList(date1, date2);

        when(getAvailableDatesUseCase.getAvailableDates()).thenReturn(availableDates);

        // When & Then
        mockMvc.perform(get("/reservations/available-dates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(date1.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[1]").value(date2.format(DateTimeFormatter.ISO_DATE_TIME)));

        verify(getAvailableDatesUseCase, times(1)).getAvailableDates();
    }

    @Test
    @DisplayName("특정 날짜의 좌석 정보를 성공적으로 조회한다")
    void testGetSeatInfoByDate_Success() throws Exception {
        // Given
        LocalDate date = LocalDate.of(2024, 10, 20);
        SeatInfoResponse seat1 = new SeatInfoResponse(1L, SeatStatus.AVAILABLE);
        SeatInfoResponse seat2 = new SeatInfoResponse(2L, SeatStatus.RESERVED);
        List<SeatInfoResponse> seatInfoResponses = Arrays.asList(seat1, seat2);

        when(getSeatInfoByDateUseCase.getSeatInfoByDate(any(LocalDateTime.class))).thenReturn(seatInfoResponses);

        // When & Then
        mockMvc.perform(get("/reservations/seats")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].seatId").value(1))
                .andExpect(jsonPath("$[0].seatStatus").value("AVAILABLE"))
                .andExpect(jsonPath("$[1].seatId").value(2))
                .andExpect(jsonPath("$[1].seatStatus").value("RESERVED"));

        verify(getSeatInfoByDateUseCase, times(1)).getSeatInfoByDate(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("좌석 예약을 성공적으로 처리한다")
    void testReserveSeat_Success() throws Exception {
        // Given
        Long seatId = 1L;
        CreateReservationRequest request = CreateReservationRequest.builder()
                .paymentId(1L)
                .scheduleId(1L)
                .userId(1L)
                .seatId(seatId)
                .reservationStatus(ReservationStatus.PENDING)
                .reservationTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build();

        // When & Then
        mockMvc.perform(post("/reservations/seats/{seatId}", seatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(createReservationService, times(1)).createReservation(any(CreateReservationRequest.class));
    }

    @Test
    @DisplayName("예약 가능한 날짜 조회 시 서버 에러 발생 시 500 오류를 반환한다")
    void testGetAvailableDates_ServerError() throws Exception {
        // Given
        when(getAvailableDatesUseCase.getAvailableDates()).thenThrow(new RuntimeException("Internal Server Error"));

        // When & Then
        mockMvc.perform(get("/reservations/available-dates"))
                .andExpect(status().isInternalServerError());

        verify(getAvailableDatesUseCase, times(1)).getAvailableDates();
    }

    @Test
    @DisplayName("잘못된 날짜 형식으로 좌석 정보 조회 요청 시 400 오류를 반환한다")
    void testGetSeatInfoByDate_InvalidDateFormat() throws Exception {
        // Given
        String invalidDate = "invalid-date-format";

        // When & Then
        mockMvc.perform(get("/reservations/seats")
                        .param("date", invalidDate))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("좌석 예약 시 잘못된 요청 데이터로 400 오류를 반환한다")
    void testReserveSeat_InvalidRequest() throws Exception {
        // Given
        Long seatId = 1L;
        CreateReservationRequest request = new CreateReservationRequest(); // 필수 필드 누락된 요청 객체

        // When & Then
        mockMvc.perform(post("/reservations/seats/{seatId}", seatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(createReservationService, times(0)).createReservation(any(CreateReservationRequest.class));
    }
}
