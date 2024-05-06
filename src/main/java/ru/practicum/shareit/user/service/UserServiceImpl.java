package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        if (isExist(user.getId())) {
            return userRepository.updateUser(user);
        }
        throw new UserNotFoundException(String.format("User with id = %s not found", user.getId()));
    }

    @Override
    public User deleteUser(long id) {
        if (isExist(id)) {
            return userRepository.deleteUser(id);
        }
        throw new UserNotFoundException(String.format("User with id = %s not found", id));
    }

    @Override
    public User getUser(long id) {
        return userRepository.getUser(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id = %s not found", id)));
    }

    @Override
    public boolean isExist(long id) {
        return userRepository.isExist(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
