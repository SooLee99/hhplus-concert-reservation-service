package org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateReservationRequest {
    private Long reservationId;
    @NotNull
    private Long paymentId;

    @NotNull
    private Long scheduleId;

    @NotNull
    private Long userId;

    @NotNull
    private Long seatId;
    private ReservationStatus reservationStatus;
    private LocalDateTime reservationTime;
    private LocalDateTime expirationTime;

    @Builder
    public CreateReservationRequest(Long reservationId, Long paymentId, Long scheduleId, Long userId, Long seatId, ReservationStatus reservationStatus, LocalDateTime reservationTime, LocalDateTime expirationTime) {
        this.reservationId = reservationId;
        this.paymentId = paymentId;
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.seatId = seatId;
        this.reservationStatus = reservationStatus;
        this.reservationTime = reservationTime;
        this.expirationTime = expirationTime;
    }
}
