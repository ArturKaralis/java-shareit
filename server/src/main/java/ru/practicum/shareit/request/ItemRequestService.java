package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestResponseDto> findAllOwn(Integer userId);

    List<ItemRequestResponseDto> findAll(Integer userId, Integer from, Integer size);

    ItemRequestResponseDto findById(Integer userId, Integer requestId);

    ItemRequestResponseDto save(Integer userId, ItemRequestDto itemRequestDto);

}
