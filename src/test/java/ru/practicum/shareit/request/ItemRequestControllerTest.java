package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private static final String USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private ItemRequestMapper itemRequestMapper;
    private ItemRequestMapper mapper = new ItemRequestMapper(new UserMapper(), new ItemMapper());
    private final ItemRequest itemRequest = ItemRequest
            .builder()
            .id(1L)
            .items(Collections.emptyList())
            .requester(User.builder().id(1).build())
            .description("testDescription")
            .build();

    @Test
    void addRequestTest() throws Exception {
        when(itemRequestMapper.fromCreateRequest(anyLong(), any())).thenReturn(itemRequest);
        when(itemRequestService.create(any())).thenReturn(itemRequest);
        ItemRequestDto expected = mapper.toDto(itemRequest);
        when(itemRequestMapper.toDto(any())).thenReturn(expected);
        String result = mvc.perform(post("/requests")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mapper.toDto(itemRequest))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.readValue(result, ItemRequestDto.class), expected);
    }

    @Test
    void getItemRequestTest() throws Exception {
        when(itemRequestService.get(anyLong(), anyLong())).thenReturn(itemRequest);
        ItemRequestDto expected = mapper.toDtoWithItems(itemRequest);
        when(itemRequestMapper.toDtoWithItems(any())).thenReturn(expected);
        String result = mvc.perform(get("/requests/{requestId}", 1L)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService).get(1L, 1L);
        assertEquals(objectMapper.readValue(result, ItemRequestDto.class), expected);
    }

    @Test
    void getItemRequestsTest() throws Exception {
        when(itemRequestService.getByRequester(anyLong(), any())).thenReturn(List.of(itemRequest));
        ItemRequestDto expected = mapper.toDtoWithItems(itemRequest);
        when(itemRequestMapper.toDtoWithItems(any())).thenReturn(expected);
        Integer from = 0;
        Integer size = 10;
        String result = mvc.perform(get("/requests/all", from, size)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService).getByRequester(1L, PageRequest.of(from / size, size, Sort.by("created").descending()));
        TypeReference<List<ItemRequestDto>> typeReference = new TypeReference<List<ItemRequestDto>>() {
        };
        assertEquals(objectMapper.readValue(result, typeReference), List.of(expected));
    }

    @Test
    void getOwnerItemRequestsTest() throws Exception {
        when(itemRequestService.getByRequester(anyLong())).thenReturn(List.of(itemRequest));
        ItemRequestDto expected = mapper.toDtoWithItems(itemRequest);
        when(itemRequestMapper.toDtoWithItems(any())).thenReturn(expected);
        String result = mvc.perform(get("/requests")
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService).getByRequester(1L);
        TypeReference<List<ItemRequestDto>> typeReference = new TypeReference<List<ItemRequestDto>>() {
        };
        assertEquals(objectMapper.readValue(result, typeReference), List.of(expected));
    }
}