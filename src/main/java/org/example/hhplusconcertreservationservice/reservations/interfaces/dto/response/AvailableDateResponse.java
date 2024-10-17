package org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AvailableDateResponse {
    private LocalDate date;
}