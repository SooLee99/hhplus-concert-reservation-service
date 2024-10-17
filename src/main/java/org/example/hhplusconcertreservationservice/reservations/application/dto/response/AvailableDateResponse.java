package org.example.hhplusconcertreservationservice.reservations.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AvailableDateResponse {
    private LocalDate availableDate;
    private int seatsAvailable;
}