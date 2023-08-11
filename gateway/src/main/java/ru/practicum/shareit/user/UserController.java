package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utility.Create;
import ru.practicum.shareit.utility.Update;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll(HttpServletRequest request) {
        log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Integer userId, HttpServletRequest request) {
        log.info("Получен {} запрос {} пользователя с ID {}", request.getMethod(), request.getRequestURI(), userId);
        return userClient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Validated(Create.class) UserDto userDto, HttpServletRequest request) {
        log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return userClient.save(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody @Validated(Update.class) UserDto userDto,
                          @PathVariable Integer userId, HttpServletRequest request) {
        log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return userClient.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Integer userId, HttpServletRequest request) {
        log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        userClient.delete(userId);
    }
}
