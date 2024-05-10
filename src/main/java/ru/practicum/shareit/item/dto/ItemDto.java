package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDto {
    private final long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final ShortBookingDto lastBooking;
    private final ShortBookingDto nextBooking;
    private final List<CommentDto> comments;
    private final Long requestId;
}
