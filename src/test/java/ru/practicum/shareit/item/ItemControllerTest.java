package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private static final String USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private ItemMapper itemMapper;
    @MockBean
    private BookingService bookingService;

    private ItemMapper mapper = new ItemMapper();

    private final ItemDto itemDTO = ItemDto.builder()
            .name("testItem")
            .description("testDescription")
            .available(true)
            .build();
    private final User user = User.builder().id(1L).build();

    private final Item item = Item.builder()
            .name("testItem")
            .description("testDescription")
            .available(true)
            .comments(Collections.emptyList())
            .owner(user)
            .build();


    private final Integer from = 0;
    private final Integer size = 10;

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(any())).thenReturn(item);
        when(itemMapper.fromDto(any(), anyLong())).thenReturn(item);
        ItemDto expected = mapper.toDtoWithComments(item);
        when(itemMapper.toDtoWithComments(any())).thenReturn(expected);

        String result = mvc.perform(post("/items")
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mapper.toDto(item))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.readValue(result, ItemDto.class), expected);
    }

    @Test
    void getItemOwnerTest() throws Exception {
        when(bookingService.getBookingForItem(any())).thenReturn(Collections.emptyList());
        when(itemService.getItem(anyLong())).thenReturn(item);
        ItemDto expected = mapper.toDto(item, null, null);
        when(itemMapper.toDto(any(), any(), any())).thenReturn(expected);
        String result = mvc.perform(get("/items/{id}", 1)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).getItem(1L);
        assertEquals(objectMapper.readValue(result, ItemDto.class), expected);
    }

    @Test
    void getItemOtherUserTest() throws Exception {
        when(bookingService.getBookingForItem(any())).thenReturn(Collections.emptyList());
        when(itemService.getItem(anyLong())).thenReturn(item);
        ItemDto expected = mapper.toDto(item, null, null);
        when(itemMapper.toDto(any(), any(), any())).thenReturn(expected);
        String result = mvc.perform(get("/items/{id}", 1)
                        .header(USER_ID, 123L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).getItem(1L);
        assertEquals(objectMapper.readValue(result, ItemDto.class), expected);
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemMapper.fromUpdateDto(any(), anyLong())).thenReturn(item);
        ItemDto expected = mapper.toDtoWithComments(item);
        when(itemMapper.toDtoWithComments(any())).thenReturn(expected);
        when(itemService.updateItem(any())).thenReturn(item);
        String result = mvc.perform(patch("/items/{id}", 1)
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mapper.toDto(item))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.readValue(result, ItemDto.class), expected);
    }

    @Test
    void getAllItemsTest() throws Exception {
        when(itemService.getItemsByOwner(anyLong())).thenReturn(List.of(item));
        when(bookingService.getBookingForItem(any())).thenReturn(Collections.emptyList());
        ItemDto expected = mapper.toDto(item, null, null);
        when(itemMapper.toDto(any(), any(), any())).thenReturn(expected);
        String result = mvc.perform(get("/items", from, size)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getItemsByOwner(1L);
        TypeReference<List<ItemDto>> typeReference = new TypeReference<List<ItemDto>>() {
        };
        assertEquals(objectMapper.readValue(result, typeReference), List.of(expected));
    }

    @Test
    void searchTest() throws Exception {
        when(itemService.searchAvailableItems(anyString())).thenReturn(List.of(item));
        when(bookingService.getBookingForItem(any())).thenReturn(Collections.emptyList());
        ItemDto expected = mapper.toDto(item, null, null);
        when(itemMapper.toDto(any(), any(), any())).thenReturn(expected);
        String result = mvc.perform(get("/items/search", from, size)
                        .header(USER_ID, 1)
                        .param("text", "search-text"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).searchAvailableItems("search-text");
        TypeReference<List<ItemDto>> typeReference = new TypeReference<List<ItemDto>>() {
        };
        assertEquals(objectMapper.readValue(result, typeReference), List.of(expected));
    }

    @Test
    void emptySearchTest() throws Exception {
        String result = mvc.perform(get("/items/search", from, size)
                        .header(USER_ID, 1)
                        .param("text", ""))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        TypeReference<List<ItemDto>> typeReference = new TypeReference<List<ItemDto>>() {
        };
        assertEquals(objectMapper.readValue(result, typeReference).size(), 0);
    }

    @Test
    void createCommentTest() throws Exception {
        final Comment comment = Comment.builder()
                .text("text")
                .author(user)
                .build();
        when(itemService.addComment(any())).thenReturn(comment);
        when(itemMapper.fromAddComment(anyLong(), anyLong(), any())).thenReturn(comment);
        final CommentDto expected = mapper.toCommentDto(comment);
        when(itemMapper.toCommentDto(any())).thenReturn(expected);
        String result = mvc.perform(post("/items/{itemId}/comment", 1)
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mapper.toCommentDto(comment))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.readValue(result, CommentDto.class), expected);
    }
}