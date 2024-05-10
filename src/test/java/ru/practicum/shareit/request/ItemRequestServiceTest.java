package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.exception.ItemRequestNotFound;
import ru.practicum.shareit.request.exception.RequesterNotFound;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper(new UserMapper(), new ItemMapper());
    private final User user =
            User.builder().id(1).name("user").email("user@email.com").build();
    //    private final UserDto userDTO = new UserDTO(1L, "user@email.com", "User");
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(user)
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requester(user)
            .description("description")
            .build();
    //    private final ItemRequestDTO itemRequestDTO = ItemRequestDTO.builder()
    //            .id(1L)
    //            .description("description")
    //            .requestor(userDTO)
    //            .items(new ArrayList<>())
    //            .build();
    private final PageRequest pageRequest = PageRequest.of(0, 10);

    @BeforeEach
    void init() {
        itemRequestService = new ItemRequestServiceImpl(userService, itemRequestRepository);
    }

    @Test
    void addItemRequestTest() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        assertEquals(itemRequest, itemRequestService.create(itemRequest));
    }

    //
//    @Test
//    void addItemRequestUserNotExistTest() {
//        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(1L, itemRequestDTO));
//    }
//
    @Test
    void getOwnerItemRequestsTest() {
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        when(userService.isExist(anyLong())).thenReturn(true);
        assertEquals(List.of(itemRequest), itemRequestService.getByRequester(1L));
    }

    //
    @Test
    void getOwnerItemRequestsUserNotExistTest() {
        assertThrows(RequesterNotFound.class, () -> itemRequestService.getByRequester(1L));
    }

    //
    @Test
    void getItemRequestsTest() {
        when(itemRequestRepository.findByRequesterIdIsNot(anyLong(), any())).thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(userService.isExist(anyLong())).thenReturn(true);
        assertEquals(List.of(itemRequest), itemRequestService.getByRequester(1L, pageRequest));
    }

    @Test
    void getItemRequestsUserNotExistTest() {
        assertThrows(RequesterNotFound.class, () -> itemRequestService.getByRequester(1L, pageRequest));
    }

    @Test
    void getRequestTest() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(userService.isExist(anyLong())).thenReturn(true);
        assertEquals(itemRequest, itemRequestService.get(1L, 1L));
    }

    //
    @Test
    void getRequestUserNotExistTest() {
        assertThrows(RequesterNotFound.class, () -> itemRequestService.get(1L, 1L));
    }

    //
    @Test
    void getRequestRequestNotExistTest() {
        assertThrows(ItemRequestNotFound.class, () -> itemRequestService.get(1L));
    }
}