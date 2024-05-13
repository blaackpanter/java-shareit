package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.exceptions.ConflictUserEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();
    private final User newUser = User.builder()
            .id(1L)
            .name("newUser")
            .email("newUser@user.com")
            .build();

    @BeforeEach
    void set() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any())).thenReturn(user);
        assertEquals(user, userService.createUser(user));
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(newUser);
        assertEquals(newUser, userService.updateUser(user));
    }

    @Test
    void updateUserConflict() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.getUserByEmail(any())).thenReturn(Optional.of(user.toBuilder().id(123L).build()));
        assertThrows(ConflictUserEmailException.class, () -> userService.updateUser(user));
    }

    @Test
    void updateUserNotExistTest() {
        User notFoundedUser = user.toBuilder().id(123L).build();
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(notFoundedUser));
        verify(userRepository, never()).save(user);
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(user));
        assertEquals(user, userService.deleteUser(1L));
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUserNotExistTest() {
        User notFoundedUser = user.toBuilder().id(123L).build();
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(123L));
        verify(userRepository, never()).delete(notFoundedUser);
    }

    @Test
    void getUserTest() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(user));
        assertEquals(user, userService.getUser(1L));
    }

    @Test
    void getUserNotExistTest() {
        assertThrows(UserNotFoundException.class, () -> userService.getUser(1));
    }

    @Test
    void getAllUsers() {
        List<User> users = List.of(user, newUser);
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    void isExist() {
        when(userRepository.existsById(eq(1L))).thenReturn(true);
        assertTrue(userService.isExist(1L));
    }
}


