package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(ItemRequest request);

    List<ItemRequest> getByRequester(long requesterId);

    List<ItemRequest> getByRequester(long requesterId, PageRequest pageRequest);

    ItemRequest get(long requesterId, long requestId);

    ItemRequest get(long requestId);
}
