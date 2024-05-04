package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(User user);

    User deleteUser(long id);

    User getUser(long id);

    boolean isExist(long id);

    List<User> getAllUsers();
}
