package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exceptions.ConflictUserEmailException;
import ru.practicum.shareit.user.exceptions.UserAlreadyExistException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private final Map<Long, User> userByIds = new ConcurrentHashMap<>();
    private final Map<String, User> userByEmails = new ConcurrentHashMap<>();

    private long nextId() {
        return ID_GENERATOR.incrementAndGet();
    }

    @Override
    public boolean isExist(long id) {
        return userByIds.containsKey(id);
    }

    @Override
    public User createUser(User user) {
        if (userByEmails.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException(String.format("User with email = %s already exits", user.getEmail()));
        }
        final User toSave = user.toBuilder()
                .id(nextId())
                .build();
        save(toSave);
        return toSave;
    }

    private void save(User user) {
        userByIds.put(user.getId(), user);
        userByEmails.put(user.getEmail(), user);
    }

    @Override
    public User updateUser(User user) {
        final User prev = userByIds.remove(user.getId());
        if (prev == null) {
            throw new UserNotFoundException(String.format("User with id = %s not found", user.getId()));
        }
        if (prev.getEmail().equals(user.getEmail())) {
            throw new ConflictUserEmailException(String.format("Email already is %s", user.getEmail()));
        }
        User.UserBuilder prevBuilder = prev.toBuilder();
        if (user.getName() != null) {
            prevBuilder.name(user.getName());
        }
        if (user.getEmail() != null) {
            prevBuilder.email(user.getEmail());
        }
        User toSave = prevBuilder.build();
        userByEmails.remove(prev.getEmail());
        save(toSave);
        return toSave;
    }

    @Override
    public User deleteUser(long id) {
        User remove = userByIds.remove(id);
        if (remove == null) {
            throw new UserNotFoundException(String.format("User with id = %s not found", id));
        }
        userByEmails.remove(remove.getEmail());
        return remove;
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(userByIds.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userByIds.values());
    }
}
