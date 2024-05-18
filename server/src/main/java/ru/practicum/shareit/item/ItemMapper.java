package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class ItemMapper {
    public Item fromDto(ItemDto itemDto, long ownerId) {
        final Long requestId = itemDto.getRequestId();
        return Item.builder()
                .owner(User.builder().id(ownerId).build())
                .description(itemDto.getDescription())
                .name(itemDto.getName())
                .available(itemDto.getAvailable())
                .request(requestId != null ? ItemRequest.builder().id(requestId).build() : null)
                .build();
    }

    public Item fromUpdateDto(ItemDto itemDto, long ownerId) {
        Item.ItemBuilder itemBuilder = Item.builder()
                .owner(User.builder().id(ownerId).build());
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

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .available(item.getAvailable())
                .description(item.getDescription())
                .name(item.getName())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public ItemDto toDtoWithComments(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .available(item.getAvailable())
                .description(item.getDescription())
                .name(item.getName())
                .comments(
                        item.getComments() == null ?
                                Collections.emptyList() :
                                item.getComments().stream().map(this::toCommentDto).collect(Collectors.toList())
                )
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public ItemDto toDto(Item item, Booking last, Booking next) {
        final ItemDto.ItemDtoBuilder builder = ItemDto.builder()
                .id(item.getId())
                .available(item.getAvailable())
                .description(item.getDescription())
                .comments(item.getComments().stream().map(this::toCommentDto).collect(Collectors.toList()))
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .name(item.getName());
        if (last != null) {
            final ShortBookingDto.ShortBookingDtoBuilder lastBooking = ShortBookingDto.builder();
            lastBooking.id(last.getId());
            lastBooking.bookerId(last.getBooker().getId());
            builder.lastBooking(lastBooking.build());
        }
        if (next != null) {
            final ShortBookingDto.ShortBookingDtoBuilder nextBooking = ShortBookingDto.builder();
            nextBooking.id(next.getId());
            nextBooking.bookerId(next.getBooker().getId());
            builder.nextBooking(nextBooking.build());
        }
        return builder.build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
    }

    public Comment fromAddComment(long authorId, long itemId, CommentDto commentDTO) {
        return Comment.builder()
                .author(User.builder().id(authorId).build())
                .item(Item.builder().id(itemId).build())
                .text(commentDTO.getText())
                .build();
    }
}
