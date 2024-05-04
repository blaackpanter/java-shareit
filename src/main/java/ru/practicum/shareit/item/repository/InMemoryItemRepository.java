package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private final Map<Long, Item> storage = new ConcurrentHashMap<>();

    private long nextId() {
        return ID_GENERATOR.incrementAndGet();
    }

    @Override
    public boolean isExist(long id) {
        return storage.containsKey(id);
    }

    @Override
    public Item createItem(Item item) {
        Item toSave = item.toBuilder()
                .id(nextId())
                .build();
        storage.put(toSave.getId(), toSave);
        return toSave;
    }

    @Override
    public Item updateItem(Item item) {
        final Item prev = storage.get(item.getId());
        if (prev == null) {
            throw new ItemNotFoundException(String.format("Item with id = %s not found", item.getId()));
        }
        final Item.ItemBuilder itemBuilder = prev.toBuilder();
        if (item.getName() != null) {
            itemBuilder.name(item.getName());
        }
        if (item.getDescription() != null) {
            itemBuilder.description(item.getDescription());
        }
        Item toSave = itemBuilder
                .available(item.isAvailable())
                .build();
        storage.put(toSave.getId(), toSave);
        return toSave;
    }

    @Override
    public Optional<Item> getItem(long itemId) {
        return Optional.ofNullable(storage.get(itemId));
    }

    @Override
    public List<Item> getItemsByOwner(long ownerId) {
        return storage.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemsByNameContains(String text) {
        return storage.values().stream()
                .filter(item -> item.getName().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemsByDescriptionContains(String text) {
        return storage.values().stream()
                .filter(item -> item.getDescription().contains(text))
                .collect(Collectors.toList());
    }
}
