package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.Status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    private BookingDto bookingDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        bookingResponseDto = new BookingResponseDto(1, "bookerName", 1, "itemName");
        bookingResponseDto.setId(1);
        bookingResponseDto.setStart(LocalDateTime.parse("2023-10-21T12:23:25"));
        bookingResponseDto.setEnd(LocalDateTime.parse("2023-12-23T12:23:25"));
        bookingResponseDto.setStatus(Status.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.parse("2023-10-21T12:23:25"));
        bookingDto.setEnd(LocalDateTime.parse("2023-12-23T12:23:25"));
        bookingDto.setItemId(1);
    }

    @Test
    public void save() throws Exception {
        when(bookingService.save(any(), anyInt()))
                .thenReturn(bookingResponseDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId())))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    public void update() throws Exception {
        bookingResponseDto.setStatus(Status.APPROVED);
        when(bookingService.update(anyInt(), anyBoolean(), anyInt()))
                .thenReturn(bookingResponseDto);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(true))
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId())))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    public void getById() throws Exception {
        when(bookingService.getById(anyInt(), anyInt()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId())))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    public void findByBookerIdAll() throws Exception {
        when(bookingService.findByBookerId(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId())))
                .andExpect(jsonPath("$[0].start", is(notNullValue())))
                .andExpect(jsonPath("$[0].end", is(notNullValue())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    public void findByBookerIdWaiting() throws Exception {
        when(bookingService.findByStateUser(anyInt(), any()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WAITING")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId())))
                .andExpect(jsonPath("$[0].start", is(notNullValue())))
                .andExpect(jsonPath("$[0].end", is(notNullValue())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    public void findByOwnerIdAll() throws Exception {
        when(bookingService.findByOwnerId(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId())))
                .andExpect(jsonPath("$[0].start", is(notNullValue())))
                .andExpect(jsonPath("$[0].end", is(notNullValue())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    public void findByOwnerIdWaiting() throws Exception {
        when(bookingService.findByStateOwner(anyInt(), any()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WAITING")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId())))
                .andExpect(jsonPath("$[0].start", is(notNullValue())))
                .andExpect(jsonPath("$[0].end", is(notNullValue())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponseDto.getItem().getName())));
    }

}