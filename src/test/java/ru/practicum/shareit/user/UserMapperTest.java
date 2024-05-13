package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserMapperTest {

    @Test
    void wrongUpdateRequest() throws Exception {
        UserMapper userMapper = new UserMapper();
        assertThrows(ResponseStatusException.class, () -> userMapper.fromCreateRequest(UserDto.builder().email("email").build()));
        assertThrows(ResponseStatusException.class, () -> userMapper.fromCreateRequest(UserDto.builder().name("name").build()));
    }
}
