package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(long bookerId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime start, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatusIsOrderByStartDesc(long ownerId, BookingStatus status, PageRequest pageRequest);

    Optional<Booking> findFirstByBookerIdAndItemIdOrderByStart(long bookerId, long itemId);

    List<Booking> findAllByItemId(long itemId);
}
