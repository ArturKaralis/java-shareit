package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(Integer userId);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto, Integer userId);

    void delete(Integer id);

}
