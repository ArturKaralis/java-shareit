package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    List<ItemResponseDto> getAll(Integer ownerId, Integer from, Integer size);

    ItemResponseDto getById(Integer itemId, Integer ownerId);

    ItemResponseDto save(ItemDto itemDto, Integer ownerId);

    ItemResponseDto update(ItemDto itemDto, Integer itemId, Integer ownerId);

    List<ItemResponseDto> search(String text, Integer from, Integer size);

    CommentResponseDto saveComment(CommentDto commentDto, Integer itemId, Integer userId);

}
