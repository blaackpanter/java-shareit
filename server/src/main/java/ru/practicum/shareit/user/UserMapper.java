package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
public class UserMapper {

    public User fromCreateRequest(UserDto request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public User fromUpdateRequest(UserDto request, long id) {
        final User.UserBuilder userBuilder = User.builder()
                .id(id);
        if (request.getName() != null && !request.getName().isBlank()) {
            userBuilder.name(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
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
