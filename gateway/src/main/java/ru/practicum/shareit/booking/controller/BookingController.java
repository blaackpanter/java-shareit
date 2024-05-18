package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    public static final String USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;


    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) long bookerId, @Valid @RequestBody CreateBookingDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "Неверное время бронирования начало = %s или конц = %s",
                            bookingDto.getStart(),
                            bookingDto.getEnd())
            );
        }
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID) long ownerId, @PathVariable @Positive long bookingId,
                                          @RequestParam(name = "approved") boolean approved) {
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID) long userId, @PathVariable @Positive long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(USER_ID) long bookerId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size,
                                                 @RequestParam(name = "state", defaultValue = "ALL")
                                                 BookingStatus bookingStatus) {
        return bookingClient.getBookingsByBooker(bookerId, bookingStatus, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID) long ownerId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size,
                                                @RequestParam(name = "state", defaultValue = "ALL")
                                                BookingStatus bookingStatus) {
        return bookingClient.getBookingsByOwner(ownerId, bookingStatus, from, size);
    }
}
