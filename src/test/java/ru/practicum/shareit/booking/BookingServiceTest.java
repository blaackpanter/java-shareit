package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.exception.BookerNotFoundException;
import ru.practicum.shareit.booking.exception.BookingNotAvailableException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.ForbiddenBookingException;
import ru.practicum.shareit.booking.exception.WrongBookerException;
import ru.practicum.shareit.booking.exception.WrongBookingStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    private final BookingMapper bookingMapper = new BookingMapper(new UserMapper(), new ItemMapper());
    private final LocalDateTime now = LocalDateTime.now();
    private final User owner = User.builder()
            .id(1L)
            .name("owner")
            .email("owner@owner.com")
            .build();
    private final User user = User.builder()
            .id(2L)
            .name("user")
            .email("user@user.com")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(owner)
            .build();
    private final CreateBookingDto createBookingDto = CreateBookingDto.builder()
            .itemId(1L)
            .start(now.plusDays(1L))
            .end(now.plusDays(2L))
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(now.plusDays(1L))
            .end(now.plusDays(2L))
            .status(BookingStatus.WAITING)
            .item(item)
            .booker(user)
            .build();
    private final PageRequest pageRequest = PageRequest.of(0, 10);

    @BeforeEach
    void set() {
        bookingService = new BookingServiceImpl(bookingRepository, itemService, userService);
    }

    @Test
    void addBookingTest() {
        when(itemService.getItem(anyLong())).thenReturn(item);
        when(userService.getUser(anyLong())).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(booking);
        assertEquals(booking, bookingService.create(bookingMapper.fromCreate(2, createBookingDto)));
    }

    @Test
    void addBookingBookerIsOwnerTest() {
        when(itemService.getItem(anyLong())).thenReturn(item);
        assertThrows(WrongBookerException.class, () -> bookingService.create(bookingMapper.fromCreate(1, createBookingDto)));
    }

    @Test
    void itemNotAvailableTest() {
        when(itemService.getItem(anyLong())).thenReturn(item.toBuilder().available(false).build());
        assertThrows(BookingNotAvailableException.class, () -> bookingService.create(bookingMapper.fromCreate(2, createBookingDto)));
    }

    @Test
    void approveBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        Booking approved = booking.toBuilder().status(BookingStatus.APPROVED).build();
        assertEquals(approved, bookingService.approve(1L, 2L, true));
    }

    @Test
    void approveBookingByNotOwnerTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        assertThrows(ForbiddenBookingException.class, () -> bookingService.approve(2L, 1L, true));
    }

    @Test
    void approveBookingAlreadyApprovedTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking.toBuilder().status(BookingStatus.APPROVED).build()));
        assertThrows(WrongBookingStatusException.class, () -> bookingService.approve(1L, 2L, true));
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        assertEquals(booking, bookingService.get(1L, 1L));
    }

    @Test
    void getBookingByBookerTest() {
        when(bookingRepository.findFirstByBookerIdAndItemIdOrderByStart(eq(2L), anyLong())).thenReturn(Optional.ofNullable(booking));
        assertEquals(booking, bookingService.getBooking(item, 2L));
        when(bookingRepository.findFirstByBookerIdAndItemIdOrderByStart(eq(123L), anyLong())).thenReturn(Optional.empty());
        assertThrows(BookerNotFoundException.class, () -> bookingService.getBooking(item, 123L));
    }

    @Test
    void getBookingForItemTest() {
        when(bookingRepository.findAllByItemId(anyLong())).thenReturn(List.of(booking));
        assertEquals(List.of(booking), bookingService.getBookingForItem(item));
    }

    @Test
    void getBookingNotExistTest() {
        assertThrows(BookingNotFoundException.class, () -> bookingService.get(1L, 123L));
    }

    @Test
    void getBookingNotOwnerTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        assertThrows(ForbiddenBookingException.class, () -> bookingService.get(3L, 1L));
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void getAllByBookerTest(BookingState bookingState) {
        when(userService.isExist(anyLong())).thenReturn(true);
        switch (bookingState) {
            case ALL:
                when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                        .thenReturn(List.of(booking));
                break;
            case PAST:
                when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(List.of(booking));
                break;
            case FUTURE:
                when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(List.of(booking));
                break;
            case CURRENT:
                when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                        any(), any(), any()))
                        .thenReturn(List.of(booking));
                break;
            case WAITING:
            case REJECTED:
                when(bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(List.of(booking));
                break;
        }
        assertEquals(List.of(booking), bookingService.getAllByBooker(2L, bookingState, pageRequest));
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void allByWrongBookerTest(BookingState bookingState) {
        when(userService.isExist(anyLong())).thenReturn(false);
        assertThrows(BookerNotFoundException.class, () -> bookingService.getAllByBooker(123L, bookingState, pageRequest));
    }

    @Test
    void allByBookerWrongStateTest() {
        when(userService.isExist(anyLong())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> bookingService.getAllByBooker(123L, BookingState.UNKNOWN, pageRequest));
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void getAllByOwnerUnknownTest(BookingState bookingState) {
        when(userService.isExist(anyLong())).thenReturn(true);
        switch (bookingState) {
            case ALL:
                when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                        .thenReturn(List.of(booking));
                break;
            case PAST:
                when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(List.of(booking));
                break;
            case FUTURE:
                when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(List.of(booking));
                break;
            case CURRENT:
                when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                        any(), any(), any()))
                        .thenReturn(List.of(booking));
                break;
            case WAITING:
            case REJECTED:
                when(bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any()))
                        .thenReturn(List.of(booking));
                break;
        }
        assertEquals(List.of(booking), bookingService.getAllByOwner(2L, bookingState, pageRequest));
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void allByWrongOwnerTest(BookingState bookingState) {
        when(userService.isExist(anyLong())).thenReturn(false);
        assertThrows(BookerNotFoundException.class, () -> bookingService.getAllByOwner(123L, bookingState, pageRequest));
    }

    @Test
    void allByOwnerWrongStateTest() {
        when(userService.isExist(anyLong())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> bookingService.getAllByOwner(123L, BookingState.UNKNOWN, pageRequest));
    }
}