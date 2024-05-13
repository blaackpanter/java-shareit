package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemMapperTest {
    private static final ItemMapper itemMapper = new ItemMapper();

    @Test
    void wrongDto() throws Exception {

        assertThrows(ResponseStatusException.class, () -> itemMapper.fromDto(ItemDto.builder().build(), -1L));
        assertThrows(ResponseStatusException.class, () -> itemMapper.fromDto(ItemDto.builder().name("name").build(), -1L));
        assertThrows(ResponseStatusException.class, () -> itemMapper.fromDto(ItemDto.builder().name("name").description("desc").build(), -1L));
        assertThrows(ResponseStatusException.class, () -> itemMapper.fromDto(ItemDto.builder().name("name").description("desc").available(true).build(), -1L));
        Item item = itemMapper.fromDto(ItemDto.builder().name("name").description("desc").available(true).build(), 1L);
        assertNotNull(item);
    }

    @Test
    void fromUpdateDtoTest() {
        ItemDto build = ItemDto.builder().name("name").description("desc").available(true).build();
        assertNotNull(itemMapper.fromUpdateDto(build, 1L));
    }

    @Test
    void toDtoWithNullComments() {
        assertNotNull(itemMapper.toDtoWithComments(Item.builder().build()));
    }

    @Test
    void toDtoTest() {
        assertNotNull(itemMapper.toDto(
                Item.builder().comments(Collections.emptyList()).build(),
                Booking.builder().id(1L).booker(User.builder().id(1).build()).build(),
                Booking.builder().id(1L).booker(User.builder().id(1).build()).build()
        ));
    }

    @Test
    void fromAddCommentTest() {
        assertNotNull(itemMapper.fromAddComment(1, 1, CommentDto.builder().text("qwerty").build()));
    }
}
