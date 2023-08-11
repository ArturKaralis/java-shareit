package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.utility.Create;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> findByBookerId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
												   @RequestParam(defaultValue = "ALL") String state,
												   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
												   @RequestParam(defaultValue = "10") @Positive Integer size,
												   HttpServletRequest request) {
		log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
		State stateEnum = State.from(state);
		return bookingClient.findByBookerId(userId, stateEnum, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findByOwnerId(@RequestHeader(value = "X-Sharer-User-Id") Integer ownerId,
												  @RequestParam(defaultValue = "ALL") String state,
												  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
												  @RequestParam(defaultValue = "10") @Positive Integer size,
												  HttpServletRequest request) {
		log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
		State stateEnum = State.from(state);
		return bookingClient.findByOwnerId(ownerId, stateEnum, from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
									  @PathVariable Integer bookingId, HttpServletRequest request) {
		log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
		return bookingClient.getById(userId, bookingId);
	}

	@PostMapping
	public ResponseEntity<Object> save(@RequestHeader(value = "X-Sharer-User-Id") Integer bookerId,
									   @RequestBody @Validated(Create.class) BookingDto bookingDto, HttpServletRequest request) {
		log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
		return bookingClient.save(bookerId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> update(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
									 @RequestParam(value = "approved") Boolean isApproved,
									 @PathVariable Integer bookingId, HttpServletRequest request) {
		log.info("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
		return bookingClient.update(userId, isApproved, bookingId);
	}

}
