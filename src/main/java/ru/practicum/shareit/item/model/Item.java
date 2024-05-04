package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class Item {
    private final long id;
    private final long ownerId;
    private final String name;
    private final String description;
    private final Boolean available;
}
