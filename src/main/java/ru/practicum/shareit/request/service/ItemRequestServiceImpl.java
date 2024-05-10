package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.exception.ItemRequestNotFound;
import ru.practicum.shareit.request.exception.RequesterNotFound;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImpl(UserService userService, ItemRequestRepository itemRequestRepository) {
        this.userService = userService;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequest create(ItemRequest request) {
        final User user = userService.getUser(request.getRequester().getId());
        return itemRequestRepository.save(
                request.toBuilder()
                        .requester(user)
                        .created(LocalDateTime.now())
                        .build()
        );
    }

    @Override
    public List<ItemRequest> getByRequester(long requesterId) {
        if (!userService.isExist(requesterId)) {
            throw new RequesterNotFound(String.format("Not found requester by %s", requesterId));
        }
        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId);
    }

    @Override
    public List<ItemRequest> getByRequester(long requesterId, PageRequest pageRequest) {
        if (!userService.isExist(requesterId)) {
            throw new RequesterNotFound(String.format("Not found requester by %s", requesterId));
        }
        return itemRequestRepository.findByRequesterIdIsNot(requesterId, pageRequest).toList();
    }

    @Override
    public ItemRequest get(long requesterId, long requestId) {
        if (!userService.isExist(requesterId)) {
            throw new RequesterNotFound(String.format("Not found requester by %s", requesterId));
        }
        return get(requestId);
    }

    @Override
    public ItemRequest get(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFound(String.format("Not found item request by id %s", requestId)));
    }
}
