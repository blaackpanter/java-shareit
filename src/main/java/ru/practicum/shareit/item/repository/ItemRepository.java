package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    boolean isExist(long id);

    Item createItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItem(long itemId);

    List<Item> getItemsByOwner(long ownerId);

    Collection<Item> getItemsByNameContainsIgnoreCase(String text);

    Collection<Item> getItemsByDescriptionContains(String text);
}
