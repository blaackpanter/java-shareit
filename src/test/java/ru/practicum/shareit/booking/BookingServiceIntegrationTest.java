package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User booker;
    private Item savedItem;
    private Booking booking;

    @BeforeAll
    void init() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build();
        User owner = userRepository.save(user1);
        User user2 = User.builder()
                .name("booker")
                .email("booker@mail.com")
                .build();
        booker = userRepository.save(user2);
        Item item = Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build();
        savedItem = itemRepository.save(item);
        booking = Booking.builder().booker(booker)
                .start(now.minusDays(2))
                .end(now.plusDays(4))
                .item(savedItem)
                .build();
    }

    @Test
    void addBookingTest() {
        Booking createdBooking = bookingService.create(booking);
        assertThat(createdBooking.getId(), notNullValue());
        assertThat(createdBooking.getItem().getId(), equalTo(savedItem.getId()));
        assertThat(createdBooking.getStart(), notNullValue());
        assertThat(createdBooking.getEnd(), notNullValue());
        assertThat(createdBooking.getStatus(), equalTo(BookingStatus.WAITING));
    }
}