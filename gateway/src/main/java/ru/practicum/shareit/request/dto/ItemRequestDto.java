package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.utility.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class ItemRequestDto {

    private Integer id;
    @NotBlank(groups = {Create.class})
    private String description;

}
