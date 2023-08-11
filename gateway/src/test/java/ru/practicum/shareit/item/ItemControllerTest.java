package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.LastBooking;
import ru.practicum.shareit.booking.model.NextBooking;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private ItemClient itemService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    private ItemResponseDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemResponseDto();
        itemDto.setId(1);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1);
        itemDto.setLastBooking(new LastBooking(1, 1));
        itemDto.setNextBooking(new NextBooking(2,1));
        itemDto.setComments(Collections.emptyList());
    }

    @Test
    public void save() throws Exception {
        when(itemService.save(any(), anyInt()))
                .thenReturn(ResponseEntity.ok(itemDto));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.lastBooking", is(notNullValue())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId())))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemDto.getLastBooking().getBookerId())))
                .andExpect(jsonPath("$.nextBooking", is(notNullValue())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDto.getNextBooking().getId())))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDto.getNextBooking().getBookerId())))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }

    @Test
    public void update() throws Exception {
        itemDto.setName("updateName");
        itemDto.setDescription("updateDescription");
        itemDto.setAvailable(false);
        when(itemService.update(any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(itemDto));
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.lastBooking", is(notNullValue())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId())))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemDto.getLastBooking().getBookerId())))
                .andExpect(jsonPath("$.nextBooking", is(notNullValue())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDto.getNextBooking().getId())))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDto.getNextBooking().getBookerId())))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }

    @Test
    public void getAll() throws Exception {
        when(itemService.getAll(anyInt(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(List.of(itemDto)));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId())))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$[0].lastBooking", is(notNullValue())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDto.getLastBooking().getId())))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemDto.getLastBooking().getBookerId())))
                .andExpect(jsonPath("$[0].nextBooking", is(notNullValue())))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemDto.getNextBooking().getId())))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemDto.getNextBooking().getBookerId())))
                .andExpect(jsonPath("$[0].comments", hasSize(0)));
    }

    @Test
    public void getById() throws Exception {
        when(itemService.getById(anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(itemDto));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.lastBooking", is(notNullValue())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId())))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemDto.getLastBooking().getBookerId())))
                .andExpect(jsonPath("$.nextBooking", is(notNullValue())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDto.getNextBooking().getId())))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDto.getNextBooking().getBookerId())))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }

    @Test
    public void search() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(List.of(itemDto)));

        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId())))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$[0].lastBooking", is(notNullValue())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDto.getLastBooking().getId())))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemDto.getLastBooking().getBookerId())))
                .andExpect(jsonPath("$[0].nextBooking", is(notNullValue())))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemDto.getNextBooking().getId())))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemDto.getNextBooking().getBookerId())))
                .andExpect(jsonPath("$[0].comments", hasSize(0)));
    }

    @Test
    public void saveComment() throws Exception {
        CommentResponseDto commentDto = new CommentResponseDto();
        commentDto.setId(1);
        commentDto.setText("text");
        commentDto.setAuthorName("name");
        when(itemService.saveComment(any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(commentDto));
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    public void getAllFromIsNegative() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(-1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllSizeIsZero() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveItemNameIsBlank() throws Exception {
        itemDto.setName(" ");
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveItemDescriptionIsBlank() throws Exception {
        itemDto.setDescription(" ");
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveItemAvailableIsBlank() throws Exception {
        itemDto.setAvailable(null);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveCommentTextIsBlank() throws Exception {
        CommentResponseDto commentDto = new CommentResponseDto();
        commentDto.setText(" ");
        when(itemService.saveComment(any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(commentDto));
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
