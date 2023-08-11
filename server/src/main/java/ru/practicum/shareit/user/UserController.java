package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.ToLog;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@ToLog
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<GetUserDto> getAll(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        return userService.getAll(from, size);
    }

    @GetMapping("/{userId}")
    public GetUserDto getById(@PathVariable long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public GetUserDto create(@RequestBody CreateUpdateUserDto createUpdateUserDto) {
        return userService.create(createUpdateUserDto);
    }

    @PatchMapping("/{userId}")
    public GetUserDto update(@PathVariable long userId,
                             @RequestBody CreateUpdateUserDto createUpdateUserDto) {
        return userService.update(userId, createUpdateUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        userService.deleteById(userId);
    }
}
