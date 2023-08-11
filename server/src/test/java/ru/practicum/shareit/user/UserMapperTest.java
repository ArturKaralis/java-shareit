package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    @Test
    public void shouldMakeUser() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@mail.ru");
        User user = UserMapper.makeUser(userDto);
        assertEquals("name", user.getName());
        assertEquals("email@mail.ru", user.getEmail());
    }

    @Test
    public void shouldMakeUserDto() {
        User user = new User();
        user.setId(1);
        user.setName("name");
        user.setEmail("email@mail.ru");
        UserDto userDto = UserMapper.makeUserDto(user);
        assertEquals(1, userDto.getId());
        assertEquals("name", userDto.getName());
        assertEquals("email@mail.ru", userDto.getEmail());
    }

    @Test
    public void shouldMakeUserDtoList() {
        User user = new User();
        user.setId(1);
        user.setName("name");
        user.setEmail("email@mail.ru");
        List<User> users = new ArrayList<>();
        users.add(user);
        List<UserDto> usersDto = UserMapper.makeUserDtoList(users);
        assertEquals(1, usersDto.size());
        assertEquals(1, usersDto.get(0).getId());
        assertEquals("name", usersDto.get(0).getName());
        assertEquals("email@mail.ru", usersDto.get(0).getEmail());
    }
}
