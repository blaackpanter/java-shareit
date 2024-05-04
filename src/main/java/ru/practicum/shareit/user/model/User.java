package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class User {
    private final long id;
    private final String name;
    private final String email;

}
