package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {

    @Test
    public void shouldMakeItemRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        ItemRequest itemRequest = ItemRequestMapper.makeItemRequest(itemRequestDto);
        assertEquals("description", itemRequest.getDescription());
    }

    @Test
    public void shouldMakeItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("description");
        itemRequest.setRequesterId(2);
        ItemRequestResponseDto itemRequestDto = ItemRequestMapper.makeItemRequestDto(itemRequest);
        assertEquals(1, itemRequestDto.getId());
        assertEquals("description", itemRequestDto.getDescription());
    }

    @Test
    public void shouldMakeItemRequestDtoList() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("description");
        itemRequest.setRequesterId(2);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);
        List<ItemRequestResponseDto> requestsDto = ItemRequestMapper.makeItemRequestDtoList(requests);
        assertEquals(1, requestsDto.size());
        assertEquals(1, requestsDto.get(0).getId());
        assertEquals("description", requestsDto.get(0).getDescription());
    }
}
