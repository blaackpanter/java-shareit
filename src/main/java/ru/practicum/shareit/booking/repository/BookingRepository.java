package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker(long bookerId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking>  findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusIs(long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStatusIs(long ownerId, BookingStatus status);
}
