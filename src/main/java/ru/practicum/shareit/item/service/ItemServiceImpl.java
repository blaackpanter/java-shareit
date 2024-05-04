package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

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
        if (userService.isExist(item.getOwnerId())) {
            return itemRepository.createItem(item);
        }
        throw new UserNotFoundException(String.format("Cant create item, because user with id = %s not found", item.getId()));
    }

    private boolean isExist(long id) {
        return itemRepository.isExist(id);
    }

    @Override
    public Item updateItem(Item item) {
        if (isExist(item.getId())) {
            return itemRepository.updateItem(item);
        }
        throw new ItemNotFoundException(String.format("Item with id = %s not found", item.getId()));
    }

    @Override
    public Item getItem(long itemId) {
        return itemRepository.getItem(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id = %s not found", itemId)));
    }

    @Override
    public List<Item> getItemsByOwner(long ownerId) {
        if (userService.isExist(ownerId)) {
            return itemRepository.getItemsByOwner(ownerId);
        }
        throw new UserNotFoundException(String.format("User with id = %s not found", ownerId));
    }

    @Override
    public List<Item> getItemsByText(String text) {
        final List<Item> result = new ArrayList<>();
        result.addAll(itemRepository.getItemsByNameContainsIgnoreCase(text));
        result.addAll(itemRepository.getItemsByDescriptionContains(text));
        return result;
    }
}
