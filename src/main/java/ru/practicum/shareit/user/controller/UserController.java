package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(
        path = "/users",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAllUsers()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public UserDto getUser(@PathVariable("id") long userId) {
        return userMapper.toDto(
                userService.getUser(userId)
        );
    }

    @DeleteMapping(value = "/{id}")
    public UserDto deleteUser(@PathVariable("id") long userId) {
        return userMapper.toDto(userService.deleteUser(userId));
    }

    @PatchMapping(value = "/{id}")
    public UserDto updateUser(@PathVariable("id") long userId, @RequestBody UserDto userDto) {
        return userMapper.toDto(
                userService.updateUser(
                        userMapper.fromUpdateRequest(userDto, userId)
                )
        );
    }


    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userMapper.toDto(
                userService.createUser(
                        userMapper.fromCreateRequest(userDto)
                )
        );
    }

}
