package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItem(long itemId);

    List<Item> getItemsByOwner(long ownerId);

    List<Item> searchAvailableItems(String text);
}
