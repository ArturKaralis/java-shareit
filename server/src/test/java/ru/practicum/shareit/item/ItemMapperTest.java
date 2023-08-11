package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    @Test
    public void shouldMakeItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(2);
        Item item = ItemMapper.makeItem(itemDto);
        assertEquals("name", item.getName());
        assertEquals("description", item.getDescription());
        assertEquals(true, item.getAvailable());
    }

    @Test
    public void shouldMakeItemDto() {
        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        ItemResponseDto itemDto = ItemMapper.makeItemDto(item);
        assertEquals(1, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
    }

    @Test
    public void shouldMakeItemDtoList() {
        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemResponseDto> itemsDto = ItemMapper.makeListItemDto(items);
        assertEquals(1, itemsDto.size());
        assertEquals(1, itemsDto.get(0).getId());
        assertEquals("name", itemsDto.get(0).getName());
        assertEquals("description", itemsDto.get(0).getDescription());
        assertEquals(true, itemsDto.get(0).getAvailable());
    }
}
