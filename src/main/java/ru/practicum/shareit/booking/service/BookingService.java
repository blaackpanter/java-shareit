package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {
    Booking create(Booking booking);

    Booking approve(long ownerId, long bookingId, boolean approved);

    Booking get(long userId, long bookingId);

    List<Booking> getAllByBooker(long bookerId, BookingState bookingState);

    List<Booking> getAllByOwner(long ownerId, BookingState bookingState);

    Booking getBooking(Item item, long bookerId);
}
