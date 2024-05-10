package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;

    private UserMapper mapper = new UserMapper();
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user")
            .email("user@email.com")
            .build();

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@email.com")
            .build();

    @Test
    void addUserTest() throws Exception {
        when(userService.createUser(any())).thenReturn(user);
        when(userMapper.fromCreateRequest(any())).thenReturn(mapper.fromCreateRequest(userDto));
        final UserDto expected = mapper.toDto(user);
        when(userMapper.toDto(any())).thenReturn(expected);
        String result = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.readValue(result, UserDto.class), expected);
    }

    @Test
    void getUserTest() throws Exception {
        when(userService.getUser(anyLong())).thenReturn(user);
        final UserDto expected = mapper.toDto(user);
        when(userMapper.toDto(any())).thenReturn(expected);
        String result = mvc.perform(get("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).getUser(1);
        assertEquals(objectMapper.readValue(result, UserDto.class), expected);
    }

    @Test
    void updateUserTest() throws Exception {
        when(userMapper.fromUpdateRequest(userDto, 1L)).thenReturn(mapper.fromUpdateRequest(userDto, 1L));
        when(userService.updateUser(any())).thenReturn(user);
        final UserDto expected = mapper.toDto(user);
        when(userMapper.toDto(any())).thenReturn(expected);
        String result = mvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.readValue(result, UserDto.class), expected);
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).deleteUser(1);
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user));
        final UserDto expected = mapper.toDto(user);
        when(userMapper.toDto(any())).thenReturn(expected);
        final List<UserDto> expectedList = List.of(expected);
        String result = mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).getAllUsers();
        TypeReference<List<UserDto>> typeReference = new TypeReference<List<UserDto>>() {
        };
        assertEquals(objectMapper.readValue(result, typeReference), expectedList);
    }
}