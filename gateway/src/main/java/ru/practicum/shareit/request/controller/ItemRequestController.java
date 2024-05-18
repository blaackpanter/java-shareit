package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid ItemRequestDto requestDto
    ) {
        return itemRequestClient.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getByRequester(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return itemRequestClient.getByRequester(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getByRequester(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return itemRequestClient.getByRequester(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @PathVariable @Positive long requestId
    ) {
        return itemRequestClient.getItemRequest(requesterId, requestId);
    }
}
