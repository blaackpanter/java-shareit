package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRequestMapperTest {

    @Test
    void wrongUpdateRequest() throws Exception {
        ItemRequestMapper itemRequestMapper = new ItemRequestMapper(new UserMapper(), new ItemMapper());
        assertNotNull(itemRequestMapper.fromCreateRequest(1, ItemRequestDto.builder().description("qwerty").build()));
    }
}
