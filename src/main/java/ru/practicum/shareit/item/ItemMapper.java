package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Service
public class ItemMapper {
    public Item fromDto(ItemDto itemDto, long ownerId) {
        validate(itemDto, ownerId);
        return Item.builder()
                .ownerId(ownerId)
                .description(itemDto.getDescription())
                .name(itemDto.getName())
                .available(itemDto.getAvailable())
                .build();
    }

    public Item fromUpdateDto(ItemDto itemDto, long ownerId) {
        Item.ItemBuilder itemBuilder = Item.builder()
                .ownerId(ownerId);
        if (itemDto.getAvailable() != null) {
            itemBuilder.available(itemDto.getAvailable());
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemBuilder.name(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            itemBuilder.description(itemDto.getDescription());
        }
        return itemBuilder
                .build();
    }

    private void validate(ItemDto itemDto, long ownerId) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is null or blank");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is null or blank");
        }
        if (itemDto.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Available is null");
        }
        if (ownerId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Wrong ownerId id=%s, must be positive", ownerId));
        }
    }

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .available(item.isAvailable())
                .description(item.getDescription())
                .name(item.getName())
                .build();
    }
}
