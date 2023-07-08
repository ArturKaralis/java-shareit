package ru.practicum.shareit.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
@Getter
@Setter
@AllArgsConstructor
public class ItemMapper {
    public GetItemDto getItemDtoFromItem(Item item) {
        return GetItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item getItemFromCreateUpdateItemDto(CreateUpdateItemDto createUpdateItemDto) {
        return Item.builder()
                .name(createUpdateItemDto.getName())
                .description(createUpdateItemDto.getDescription())
                .available(createUpdateItemDto.getAvailable())
                .build();
    }
}
