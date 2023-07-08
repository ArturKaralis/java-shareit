package ru.practicum.shareit.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
@Getter
@Setter
@AllArgsConstructor
public class UserMapper {
    public GetUserDto toGetUserDtoFromUser(User user) {
        return GetUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUserFromCreateUpdateUserDto(CreateUpdateUserDto createUpdateUserDto) {
        return User.builder()
                .name(createUpdateUserDto.getName())
                .email(createUpdateUserDto.getEmail())
                .build();
    }
}
