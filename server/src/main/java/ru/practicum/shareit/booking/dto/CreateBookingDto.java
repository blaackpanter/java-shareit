package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBookingDto {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
