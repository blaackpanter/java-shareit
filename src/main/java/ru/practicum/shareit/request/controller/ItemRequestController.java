package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-item-requests.
 */
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService, ItemRequestMapper itemRequestMapper) {
        this.itemRequestService = itemRequestService;
        this.itemRequestMapper = itemRequestMapper;
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid ItemRequestDto requestDto
    ) {
        return itemRequestMapper.toDto(itemRequestService.create(itemRequestMapper.fromCreateRequest(userId, requestDto)));
    }

    @GetMapping
    public List<ItemRequestDto> getByRequester(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return itemRequestService.getByRequester(requesterId)
                .stream()
                .map(itemRequestMapper::toDtoWithItems)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getByRequester(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return itemRequestService.getByRequester(
                        requesterId,
                        PageRequest.of(from / size, size, Sort.by("created").descending())
                )
                .stream()
                .map(itemRequestMapper::toDtoWithItems)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @PathVariable @Positive long requestId
    ) {
        return itemRequestMapper.toDtoWithItems(
                itemRequestService.get(requesterId, requestId)
        );
    }
}
