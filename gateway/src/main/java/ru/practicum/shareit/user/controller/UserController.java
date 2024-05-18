package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(
        path = "/users"
)
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") long userId) {
        return userClient.getUser(userId);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable("id") long userId) {
        userClient.deleteUser(userId);
    }

    @PatchMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> updateUser(@PathVariable("id") long userId, @RequestBody UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }


    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is null or blank");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is null or blank");
        }
        return userClient.createUser(userDto);
    }

}
