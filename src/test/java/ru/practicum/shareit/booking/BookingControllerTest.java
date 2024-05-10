package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingMapper bookingMapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;
    private BookingMapper mapper = new BookingMapper(new UserMapper(), new ItemMapper());

    private final Item item = Item.builder()
            .name("item")
            .description("description")
            .available(true)
            .build();
    private final User user = User.builder()
            .id(1)
            .name("name")
            .email("email@email.com")
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(item)
            .booker(user)
            .build();
    private final CreateBookingDto createBookingDto = CreateBookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .build();

    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    void createBookingWhenAllParamsIsValidTest() throws Exception {
        when(bookingService.create(any())).thenReturn(booking);
        when(bookingMapper.fromCreate(anyLong(), any())).thenReturn(mapper.fromCreate(1L, createBookingDto));
        final BookingDto expected = mapper.toDto(booking);
        when(bookingMapper.toDto(any())).thenReturn(expected);
        String result = mvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.readValue(result, BookingDto.class), expected);
    }

    @Test
    void createBookingWhenStartIsNotValidTest() throws Exception {
        final CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        mvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).create(any());
    }

    @Test
    void getAllBookingsTest() throws Exception {
        Integer from = 0;
        Integer size = 10;
        String state = "ALL";
        BookingDto expected = mapper.toDto(booking);
        List<BookingDto> expectedList = List.of(expected);
        when(bookingService.getAllByBooker(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(bookingMapper.toDto(any())).thenReturn(expected);
        String result = mvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        TypeReference<List<BookingDto>> typeReference = new TypeReference<List<BookingDto>>() {
        };
        assertEquals(objectMapper.readValue(result, typeReference), expectedList);
    }

    @Test
    void getAllByOwnerBookingsTest() throws Exception {
        String state = "ALL";
        BookingDto expected = mapper.toDto(booking);
        List<BookingDto> expectedList = List.of(expected);
        when(bookingService.getAllByOwner(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(bookingMapper.toDto(any())).thenReturn(expected);
        Integer from = 0;
        String result = mvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(10))
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        TypeReference<List<BookingDto>> typeReference = new TypeReference<List<BookingDto>>() {
        };
        assertEquals(objectMapper.readValue(result, typeReference), expectedList);
    }

    @Test
    void approveBookingTest() throws Exception {
        BookingDto expected = mapper.toDto(booking);
        when(bookingMapper.toDto(any())).thenReturn(expected);
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);
        String result = mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", String.valueOf(true))
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.readValue(result, BookingDto.class), expected);
    }

    @Test
    void getBookingByIdTest() throws Exception {
        BookingDto expected = mapper.toDto(booking);
        when(bookingMapper.toDto(any())).thenReturn(expected);
        when(bookingService.get(anyLong(), anyLong())).thenReturn(booking);
        String result = mvc.perform(get("/bookings/{bookingId}", 1)
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.readValue(result, BookingDto.class), expected);
    }
}