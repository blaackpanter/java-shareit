package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.BookerNotFoundException;
import ru.practicum.shareit.booking.exception.BookingNotAvailableException;
import ru.practicum.shareit.booking.exception.ForbiddenBookingException;
import ru.practicum.shareit.booking.exception.WrongBookerException;
import ru.practicum.shareit.booking.exception.WrongBookingIdException;
import ru.practicum.shareit.booking.exception.WrongBookingStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    public Booking create(Booking booking) {
        final Item item = itemService.getItem(booking.getItem().getId());
        if (!item.getAvailable()) {
            throw new BookingNotAvailableException("Вещь не доступна для бронирования");
        }
        if (item.getOwner().getId() == booking.getBooker().getId()) {
            throw new WrongBookerException("Владелец не может забронировать свою вещь");
        }
        final User booker = userService.getUser(booking.getBooker().getId());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    private Booking getById(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new WrongBookingIdException(String.format("Не найдено бронирование с id %s", bookingId)));
    }

    @Override
    public Booking approve(long ownerId, long bookingId, boolean approved) {
        final Booking booking = getById(bookingId);
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ForbiddenBookingException("Только владелец может разрешить/запретить бронировани");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new WrongBookingStatusException("Бронирование уже разрешено, запрещено или отменено");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking get(long userId, long bookingId) {
        final Booking booking = getById(bookingId);
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new ForbiddenBookingException("Только владелец или арендатор может получить информацию о бронировании");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllByBooker(long bookerId, BookingState bookingState) {
        if (!userService.isExist(bookerId)) {
            throw new BookerNotFoundException("Арендатор не найден");
        }
        final LocalDateTime now = LocalDateTime.now();
        final List<Booking> result;
        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByBookerId(bookerId);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(bookerId, now, now);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndIsBefore(bookerId, now);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartIsAfter(bookerId, now);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusIs(bookerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusIs(bookerId, BookingStatus.REJECTED);
                break;
            default:
                throw new RuntimeException(String.format("Неизвестное состояние бронирования %s", bookingState));
        }
        return result.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> getAllByOwner(long ownerId, BookingState bookingState) {
        if (!userService.isExist(ownerId)) {
            throw new BookerNotFoundException("Владелец не найден");
        }
        final LocalDateTime now = LocalDateTime.now();
        final List<Booking> result;
        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerId(ownerId);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, now, now);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(ownerId, now);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(ownerId, now);
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new RuntimeException(String.format("Неизвестное состояние бронирования %s", bookingState));
        }
        return result.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Booking getBooking(Item item, long bookerId) {
        return bookingRepository.findByBookerIdAndItemId(bookerId, item.getId())
                .orElseThrow(
                        () -> new BookerNotFoundException(
                                String.format("Пользователь %s не бронировал предмет %s", bookerId, item)
                        )
                );
    }
}
