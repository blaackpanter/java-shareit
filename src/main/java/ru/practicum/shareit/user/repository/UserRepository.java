package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    boolean isExist(long id);

    User createUser(User user);

    User updateUser(User user);

    User deleteUser(long id);

    Optional<User> getUser(long id);

    List<User> getAllUsers();
}
