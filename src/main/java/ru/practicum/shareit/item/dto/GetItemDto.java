package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
