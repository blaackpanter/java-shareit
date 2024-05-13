package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.WrongCommentDateException;
import ru.practicum.shareit.item.exceptions.WrongOwnerIdException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;

    private final LocalDateTime now = LocalDateTime.now();

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(user)
            .request(null)
            .comments(new ArrayList<>())
            .build();

    @BeforeEach
    void init() {
        itemService = new ItemServiceImpl(itemRepository, userService, itemRequestService, bookingService, commentRepository);
    }

    @Test
    void createItemTest() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);
        assertEquals(item, itemService.createItem(item));
    }

    @Test
    void addItemWithRequestTest() {
        final ItemRequest itemRequest = ItemRequest.builder()
                .requester(user)
                .id(1)
                .description("desc")
                .created(LocalDateTime.now())
                .build();
        Item itemWithRequest = item.toBuilder().request(itemRequest).build();
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(itemWithRequest);
        when(itemRequestService.get(anyLong())).thenReturn(itemRequest);
        assertEquals(itemWithRequest, itemService.createItem(itemWithRequest));
    }

    @Test
    void updateItemTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any())).thenReturn(item);
        assertEquals(item, itemService.updateItem(item));
    }

    @Test
    void updateItemNotExistTest() {
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(item));
    }

    @Test
    void updateItemNotOwnerExistTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        Item wrondOwner = item.toBuilder().owner(user.toBuilder().id(123L).build()).build();
        assertThrows(WrongOwnerIdException.class, () -> itemService.updateItem(wrondOwner));
    }

    @Test
    void getItemByOwnerTest() {
        when(userService.isExist(anyLong())).thenReturn(true);
        List<Item> expected = List.of(item);
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(expected);
        assertEquals(expected, itemService.getItemsByOwner(1L));
    }

    @Test
    void getItemByOwnerNotFoundTest() {
        when(userService.isExist(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemService.getItemsByOwner(1L));
    }

    @Test
    void findItemsByText() {
        when(itemRepository.findAllByDescriptionContainingIgnoreCase(anyString())).thenReturn(List.of(item));
        when(itemRepository.findAllByNameContainingIgnoreCase(anyString())).thenReturn(List.of(item));
        List<Item> expected = List.of(item);
        assertEquals(expected, itemService.searchAvailableItems("text"));
    }

    @Test
    void addCommentTest() {


        Comment comment = Comment.builder()
                .id(1L)
                .text("Text")
                .author(user)
                .item(item)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(now.minusDays(1L))
                .end(now.plusDays(2L))
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingService.getBooking(any(), anyLong())).thenReturn(booking);
        when(commentRepository.save(any())).thenReturn(comment);
        assertEquals(comment, itemService.addComment(comment));
    }

    @Test
    void addCommentBadTimeTest() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Text")
                .author(user)
                .item(item)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(now.plusDays(10L))
                .end(now.plusDays(20L))
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingService.getBooking(any(), anyLong())).thenReturn(booking);
        assertThrows(WrongCommentDateException.class, () -> itemService.addComment(comment));
    }
}