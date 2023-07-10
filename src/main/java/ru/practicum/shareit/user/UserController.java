package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnUpdate;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<GetUserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public GetUserDto getById(@PathVariable long userId) {
        log.info("Получение пользователя по ID: {}", userId);
        return userService.getById(userId);
    }


    @PostMapping
    public GetUserDto create(@RequestBody @Validated(OnCreate.class) CreateUpdateUserDto createUpdateUserDto) {
        log.info("Запрос на добавление пользователя");
        return userService.create(createUpdateUserDto);
    }

    @PatchMapping("/{userId}")
    public GetUserDto update(@PathVariable long userId,
                             @RequestBody @Validated(OnUpdate.class) CreateUpdateUserDto createUpdateUserDto) {
        log.info("Получить пользователя: {}", createUpdateUserDto);
        return userService.update(userId, createUpdateUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        log.info("Удалить пользователя: {}", userId);
        userService.deleteById(userId);
    }
}
