package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
