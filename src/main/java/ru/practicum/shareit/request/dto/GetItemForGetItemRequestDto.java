package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetItemForGetItemRequestDto {
    private Long id;
    private String name;
    private String description;
    private Long requestId;
    private Boolean available;
}