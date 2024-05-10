package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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
    public BookingServiceImpl(BookingRepository bookingRepository, @Lazy ItemService itemService, UserService userService) {
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
                .orElseThrow(() -> new BookingNotFoundException(String.format("Не найдено бронирование с id %s", bookingId)));
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
    public List<Booking> getAllByBooker(long bookerId, BookingState bookingState, PageRequest pageRequest) {
        if (!userService.isExist(bookerId)) {
            throw new BookerNotFoundException("Арендатор не найден");
        }
        final LocalDateTime now = LocalDateTime.now();
        final List<Booking> result;
        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageRequest);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId, now, now, pageRequest);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, now, pageRequest);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, now, pageRequest);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(bookerId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(bookerId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                throw new RuntimeException(String.format("Неизвестное состояние бронирования %s", bookingState));
        }
        return result;
    }

    @Override
    public List<Booking> getAllByOwner(long ownerId, BookingState bookingState, PageRequest pageRequest) {
        if (!userService.isExist(ownerId)) {
            throw new BookerNotFoundException("Владелец не найден");
        }
        final LocalDateTime now = LocalDateTime.now();
        final List<Booking> result;
        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageRequest);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, now, now, pageRequest);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, now, pageRequest);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, now, pageRequest);
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(ownerId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                throw new RuntimeException(String.format("Неизвестное состояние бронирования %s", bookingState));
        }
        return result;
    }

    @Override
    public Booking getBooking(Item item, long bookerId) {
        return bookingRepository.findFirstByBookerIdAndItemIdOrderByStart(bookerId, item.getId())
                .orElseThrow(
                        () -> new BookerNotFoundException(
                                String.format("Пользователь %s не бронировал предмет %s", bookerId, item)
                        )
                );
    }

    @Override
    public List<Booking> getBookingForItem(Item item) {
        return bookingRepository.findAllByItemId(item.getId());
    }
}
