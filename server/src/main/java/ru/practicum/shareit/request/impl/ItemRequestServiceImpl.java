package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemRequestResponseDto> findAllOwn(Integer userId) {
        getUserOrException(userId);
        List<ItemRequestResponseDto> requestsDto = ItemRequestMapper.makeItemRequestDtoList(itemRequestRepository
                .findByRequesterId(userId, Sort.by(DESC, "created")));
        return setItemsForAllOwn(requestsDto, userId);
    }

    @Override
    public List<ItemRequestResponseDto> findAll(Integer userId, Integer from, Integer size) {
        getUserOrException(userId);
        List<ItemRequestResponseDto> requestsDto = ItemRequestMapper.makeItemRequestDtoList(
                itemRequestRepository.findAllExceptUserId(userId,
                        PageRequest.of(from / size, size, Sort.by(DESC, "created"))).getContent());
        return setItemsForAll(requestsDto, userId);
    }

    public List<ItemRequestResponseDto> setItemsForAllOwn(List<ItemRequestResponseDto> requestsDto, Integer userId) {
        Map<Integer, List<Item>> items = itemRepository.findAllByRequesterId(userId)
                .stream().collect(groupingBy(item -> item.getItemRequest().getId()));
        for (ItemRequestResponseDto itemRequestDto : requestsDto) {
            if (items.containsKey(itemRequestDto.getId())) {
                itemRequestDto.setItems(ItemMapper.makeListItemDto(items.get(itemRequestDto.getId())));
            } else {
                itemRequestDto.setItems(Collections.emptyList());
            }
        }
        return requestsDto;
    }

    public List<ItemRequestResponseDto> setItemsForAll(List<ItemRequestResponseDto> requestsDto, Integer userId) {
        Map<Integer, List<Item>> items = itemRepository.findAllNotEqualRequesterId(userId)
                .stream().collect(groupingBy(item -> item.getItemRequest().getId()));
        for (ItemRequestResponseDto itemRequestDto : requestsDto) {
            if (items.containsKey(itemRequestDto.getId())) {
                itemRequestDto.setItems(ItemMapper.makeListItemDto(items.get(itemRequestDto.getId())));
            } else {
                itemRequestDto.setItems(Collections.emptyList());
            }
        }
        return requestsDto;
    }

    @Override
    public ItemRequestResponseDto findById(Integer userId, Integer requestId) {
        getUserOrException(userId);
        ItemRequestResponseDto itemRequestDto = ItemRequestMapper.makeItemRequestDto(
                itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest with id " + requestId)));
        List<ItemResponseDto> items = ItemMapper.makeListItemDto(itemRepository.findByRequestId(requestId));
        itemRequestDto.setItems(Objects.requireNonNullElse(items, Collections.emptyList()));
        return itemRequestDto;
    }

    @Override
    @Transactional
    public ItemRequestResponseDto save(Integer userId, ItemRequestDto itemRequestDto) {
        getUserOrException(userId);
        ItemRequest itemRequest = ItemRequestMapper.makeItemRequest(itemRequestDto);
        itemRequest.setRequesterId(userId);
        return ItemRequestMapper.makeItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    private void getUserOrException(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId));
    }

}
