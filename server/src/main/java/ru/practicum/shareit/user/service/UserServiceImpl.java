package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.exceptions.ConflictUserEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        final User existedUser = getUser(user.getId());
        if (user.getName() != null) {
            existedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            final Optional<User> userByEmail = userRepository.getUserByEmail(user.getEmail());
            if (userByEmail.isPresent() && userByEmail.get().getId() != user.getId()) {
                throw new ConflictUserEmailException(String.format("User with email %s already exist", user.getEmail()));
            }
            existedUser.setEmail(user.getEmail());
        }
        return userRepository.save(existedUser);
    }

    @Override
    @Transactional
    public User deleteUser(long id) {
        final User user = getUser(id);
        userRepository.delete(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id = %s not found", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExist(long id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
