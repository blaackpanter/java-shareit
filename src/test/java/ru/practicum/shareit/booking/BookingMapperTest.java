package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.exception.WrongBookingDateException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingMapperTest {

    @Test
    void wrongDatesTest() throws Exception {
        BookingMapper bookingMapper = new BookingMapper(new UserMapper(), new ItemMapper());
        CreateBookingDto createBookingDto = CreateBookingDto.builder().start(LocalDateTime.now()).end(LocalDateTime.now().minusDays(2)).build();
        assertThrows(WrongBookingDateException.class, () -> bookingMapper.fromCreate(1, createBookingDto));
    }
}
