package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(
        path = "/items"
)
public class ItemController {
    private final ItemMapper itemMapper;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Autowired
    public ItemController(ItemMapper itemMapper, ItemService itemService, BookingService bookingService) {
        this.itemMapper = itemMapper;
        this.itemService = itemService;
        this.bookingService = bookingService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemMapper.toDto(
                itemService.createItem(
                        itemMapper.fromDto(
                                itemDto,
                                ownerId
                        )
                )
        );
    }

    @PatchMapping(
            value = "/{itemId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ItemDto updateItem(
            @PathVariable("itemId") long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        final Item item = itemMapper.fromUpdateDto(itemDto, ownerId).toBuilder().id(itemId).build();
        return itemMapper.toDto(
                itemService.updateItem(item)
        );
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable("itemId") long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        final Item item = itemService.getItem(itemId);
        final List<Booking> bookingForItem = bookingService.getBookingForItem(item);
        final Booking lastBooking;
        final Booking nextBooking;
        if (userId == item.getOwner().getId()) {
            final LocalDateTime now = LocalDateTime.now();
            lastBooking = bookingForItem.stream()
                    .filter(b -> b.getStart().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);
            nextBooking = bookingForItem.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        } else {
            lastBooking = null;
            nextBooking = null;
        }
        return itemMapper.toDto(
                item,
                lastBooking,
                nextBooking
        );
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getItemsByOwner(ownerId)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("search")
    public List<ItemDto> searchAvailableItems(@RequestParam("text") String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchAvailableItems(text)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                 @RequestBody @Valid CommentDto commentDTO) {
        return itemMapper.toCommentDto(itemService.addComment(itemMapper.fromAddComment(userId, itemId, commentDTO)));
    }
}
