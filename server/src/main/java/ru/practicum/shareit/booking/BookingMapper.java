package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.exception.WrongBookingDateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
public class BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public BookingMapper(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    public Booking fromCreate(long bookerId, CreateBookingDto bookingDto) {
        return Booking.builder()
                .item(Item.builder().id(bookingDto.getItemId()).build())
                .booker(User.builder().id(bookerId).build())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .booker(userMapper.toDto(booking.getBooker()))
                .item(itemMapper.toDto(booking.getItem()))
                .status(booking.getStatus())
                .end(booking.getEnd())
                .start(booking.getStart())
                .build();
    }
}
