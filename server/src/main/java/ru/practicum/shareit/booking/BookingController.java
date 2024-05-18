package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    public static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID) long bookerId, @Valid @RequestBody CreateBookingDto bookingDto) {
        final Booking booking = bookingMapper.fromCreate(bookerId, bookingDto);
        return bookingMapper.toDto(bookingService.create(booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_ID) long ownerId, @PathVariable @Positive long bookingId,
                              @RequestParam(name = "approved") boolean approved) {
        return bookingMapper.toDto(bookingService.approve(ownerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(USER_ID) long userId, @PathVariable @Positive long bookingId) {
        return bookingMapper.toDto(bookingService.get(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader(USER_ID) long bookerId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size,
                                           @RequestParam(name = "state", defaultValue = "ALL")
                                           BookingState bookingState) {
        return bookingService.getAllByBooker(bookerId, bookingState, PageRequest.of(from / size, size))
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(USER_ID) long ownerId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "10") @Positive int size,
                                          @RequestParam(name = "state", defaultValue = "ALL")
                                          BookingState bookingState) {
        return bookingService.getAllByOwner(ownerId, bookingState, PageRequest.of(from / size, size))
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
