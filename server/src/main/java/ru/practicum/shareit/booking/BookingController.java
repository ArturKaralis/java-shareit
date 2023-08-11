package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.State;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponseDto> findByBookerId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                    @RequestParam(defaultValue = "ALL") String state,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    HttpServletRequest request) {
        log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        State stateEnum = State.valueOf(state);
        if (stateEnum.equals(State.ALL)) {
            return bookingService.findByBookerId(userId, from, size);
        }
        return bookingService.findByStateUser(userId, stateEnum);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findByOwnerId(@RequestHeader(value = "X-Sharer-User-Id") Integer ownerId,
                                    @RequestParam(defaultValue = "ALL") String state,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    HttpServletRequest request) {
        log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        State stateEnum = State.valueOf(state);
        if (stateEnum.equals(State.ALL)) {
            return bookingService.findByOwnerId(ownerId, from, size);
        }
        return bookingService.findByStateOwner(ownerId, stateEnum);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                           @PathVariable Integer bookingId, HttpServletRequest request) {
        log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return bookingService.getById(userId, bookingId);
    }

    @PostMapping
    public BookingResponseDto save(@RequestHeader(value = "X-Sharer-User-Id") Integer bookerId,
                           @RequestBody BookingDto bookingDto, HttpServletRequest request) {
        log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return bookingService.save(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                           @RequestParam(value = "approved") Boolean isApproved,
                           @PathVariable Integer bookingId, HttpServletRequest request) {
        log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return bookingService.update(userId, isApproved, bookingId);
    }

}
