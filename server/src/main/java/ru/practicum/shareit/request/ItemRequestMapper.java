package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

@Service
public class ItemRequestMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemRequestMapper(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }


    public ItemRequest fromCreateRequest(long requesterId, ItemRequestDto request) {
        return ItemRequest.builder()
                .description(request.getDescription())
                .requester(User.builder().id(requesterId).build())
                .build();
    }

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .requester(userMapper.toDto(itemRequest.getRequester()))
                .description(itemRequest.getDescription())
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequestDto toDtoWithItems(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .requester(userMapper.toDto(itemRequest.getRequester()))
                .description(itemRequest.getDescription())
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems().stream().map(itemMapper::toDto).collect(Collectors.toList()))
                .build();
    }
}
