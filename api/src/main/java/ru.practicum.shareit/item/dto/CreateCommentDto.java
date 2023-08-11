package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {

    @NotBlank
    @Size(max = 1000, groups = {OnCreate.class, OnUpdate.class})
    private String text;
}
