package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return UserMapper.makeUserDtoList(userRepository.findAll());
    }

    @Override
    public UserDto getById(Integer userId) {
        return UserMapper.makeUserDto(getUserOrException(userId));
    }

    @Override
    @Transactional
    public UserDto save(UserDto userdto) {
        return UserMapper.makeUserDto(userRepository.save(UserMapper.makeUser(userdto)));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Integer userId) {
        User savedUser = getUserOrException(userId);
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            savedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            savedUser.setName(userDto.getName());
        }
        return UserMapper.makeUserDto(savedUser);
    }

    @Override
    public void delete(Integer userId) {
        userRepository.deleteById(userId);
    }

    private User getUserOrException(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId));
    }

}