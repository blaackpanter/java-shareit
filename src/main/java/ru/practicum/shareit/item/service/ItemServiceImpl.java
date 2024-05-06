package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.WrongOwnerIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public Item createItem(Item item) {
        final User owner = userService.getUser(item.getOwner().getId());
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private boolean isExist(long id) {
        return itemRepository.existsById(id);
    }

    @Override
    public Item updateItem(Item item) {
        final Item prev = getItem(item.getId());
        if (prev.getOwner().getId() != item.getOwner().getId()) {
            throw new WrongOwnerIdException(String.format("Only owner can update item with id %s", item.getId()));
        }
        if (item.getName() != null) {
            prev.setName(item.getName());
        }
        if (item.getDescription() != null) {
            prev.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            prev.setAvailable(item.getAvailable());
        }
        return itemRepository.save(prev);
    }

    @Override
    public Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id = %s not found", itemId)));
    }

    @Override
    public List<Item> getItemsByOwner(long ownerId) {
        if (userService.isExist(ownerId)) {
            return itemRepository.findAllByOwnerId(ownerId);
        }
        throw new UserNotFoundException(String.format("User with id = %s not found", ownerId));
    }

    @Override
    public List<Item> searchAvailableItems(String text) {
        final List<Item> result = new ArrayList<>();
        result.addAll(itemRepository.findAllByNameContainingIgnoreCase(text));
        result.addAll(itemRepository.findAllByDescriptionContainingIgnoreCase(text));
        return result.stream()
                .distinct()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
