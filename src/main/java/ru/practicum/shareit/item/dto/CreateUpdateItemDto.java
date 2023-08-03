package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.marker.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateUpdateItemDto {

    @NotBlank(groups = OnCreate.class)
    @Size(max = 255)
    private String name;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 512)
    private String description;

    @NotNull(groups = OnCreate.class)
    private Boolean available;
}
