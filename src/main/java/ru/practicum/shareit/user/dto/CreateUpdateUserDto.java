package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Getter
@Setter
@AllArgsConstructor
public class CreateUpdateUserDto {
    @NotBlank(groups = OnCreate.class)
    private String name;

    @NotBlank(groups = OnCreate.class)
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
            message = "Email не корректный",
            groups = {OnCreate.class, OnUpdate.class})
    private String email;
}
