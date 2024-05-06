package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
public class UserMapper {

    public User fromCreateRequest(UserDto request) {
        validate(request);
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    private void validate(UserDto request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is null or blank");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is null or blank");
        }
    }

    public User fromUpdateRequest(UserDto request, long id) {
        final User.UserBuilder userBuilder = User.builder()
                .id(id);
        if (request.getName() != null) {
            userBuilder.name(request.getName());
        }
        if (request.getEmail() != null) {
            userBuilder.email(request.getEmail());
        }
        return userBuilder
                .build();
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
