package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler geh = new GlobalExceptionHandler();

    @Test
    public void handleNotFoundException() {
        ErrorResponse er = geh.handleNotFoundException(new NotFoundException("test message"));
        assertEquals("test message not found", er.getError());
    }

    @Test
    public void handleAlreadyExistException() {
        ErrorResponse er = geh.handleAlreadyExistException(new AlreadyExistException("test message"));
        assertEquals("test message already exist", er.getError());
    }

    @Test
    public void handleBadRequestExceptionBookingDateTime() {
        ResponseEntity<String> re = geh.handleBadRequestException(new BookingDateTimeException("test message"));
        assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
        assertEquals("test message", re.getBody());
    }

    @Test
    public void handleBadRequestExceptionItem() {
        ResponseEntity<String> re = geh.handleBadRequestException(new ItemNotAvailableException("test message"));
        assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
        assertEquals("test message", re.getBody());
    }

    @Test
    public void handleBadRequestException() {
        ErrorResponse er = geh.handleBadRequestException(new BookingStatusException("test message"));
        assertEquals("test message", er.getError());
    }

    @Test
    public void handleBadRequestExceptionBooking() {
        ResponseEntity<String> re = geh.handleBadRequestException(new BookingException("test message"));
        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
        assertEquals("test message", re.getBody());
    }

    @Test
    public void throwableException() {
        ResponseEntity<String> re = geh.throwableException(new Throwable("test message"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, re.getStatusCode());
        assertEquals("test message", re.getBody());
    }

}
