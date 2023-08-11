package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingDateTimeException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemResponseDto> getAll(Integer ownerId, Integer from, Integer size) {
        List<ItemResponseDto> items = ItemMapper.makeListItemDto(itemRepository.findAllByOwnerId(
                ownerId, PageRequest.of(from / size, size, Sort.by(ASC, "id"))).getContent());
        Map<Integer, List<Comment>> comments = commentRepository.findAll().stream()
                .collect(groupingBy(comment -> comment.getItem().getId()));
        for (ItemResponseDto item : items) {
            if (comments.containsKey(item.getId())) {
                item.setComments(CommentMapper.makeListCommentDto(comments.get(item.getId())));
            }
        }
        Map<Integer, List<Booking>> pastBooking = getLastBookings(ownerId);
        Map<Integer, List<Booking>> futureBooking = getNextBookings(ownerId);
        for (ItemResponseDto itemDto : items) {
            if (pastBooking.containsKey(itemDto.getId())) {
                setLastBooking(itemDto, pastBooking.get(itemDto.getId()));
            }
            if (futureBooking.containsKey(itemDto.getId())) {
                setNextBooking(itemDto, futureBooking.get(itemDto.getId()));
            }
        }
        return items;
    }

    @Override
    public ItemResponseDto getById(Integer itemId, Integer userId) {
        getUserOrException(userId);
        Item item = getItemOrException(itemId);
        ItemResponseDto itemDto = ItemMapper.makeItemDto(item);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            Map<Integer, List<Booking>> pastBooking = getLastBookings(userId);
            Map<Integer, List<Booking>> futureBooking = getNextBookings(userId);
            if (pastBooking.containsKey(itemDto.getId())) {
                setLastBooking(itemDto, pastBooking.get(itemDto.getId()));
            }
            if (futureBooking.containsKey(itemDto.getId())) {
                setNextBooking(itemDto, futureBooking.get(itemDto.getId()));
            }
        }
        List<Comment> comments = commentRepository.findByItemId(itemDto.getId());
        itemDto.setComments(CommentMapper.makeListCommentDto(comments));
        return itemDto;
    }

    @Override
    @Transactional
    public ItemResponseDto save(ItemDto itemDto, Integer ownerId) {
        User user = getUserOrException(ownerId);
        Item item = ItemMapper.makeItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemDto.getRequestId());
            itemRequest.ifPresent(item::setItemRequest);
        }
        return ItemMapper.makeItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemResponseDto update(ItemDto itemDto, Integer itemId, Integer ownerId) {
        Item item = getItemOrException(itemId);
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new NotFoundException("Item owner");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.makeItemDto(item);
    }

    @Override
    @Transactional
    public CommentResponseDto saveComment(CommentDto commentDto, Integer itemId, Integer userId) {
        Item item = getItemOrException(itemId);
        User user = getUserOrException(userId);
        List<Booking> pastBooking = bookingRepository.findPastBookingByItemId(LocalDateTime.now(), itemId);
        if (pastBooking.isEmpty()) {
            throw new BookingDateTimeException("Item without previous booking");
        }
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setText(commentDto.getText());
        comment.setAuthorName(user.getName());
        return CommentMapper.makeCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemResponseDto> search(String text, Integer from, Integer size) {
        return ItemMapper.makeListItemDto(itemRepository
                .search(text, PageRequest.of(from / size, size)).getContent());
    }

    private Map<Integer, List<Booking>> getLastBookings(Integer userId) {
        return bookingRepository.findPastBookingOwner(LocalDateTime.now(), userId,
                        Sort.by(DESC, "end"))
                .stream().collect(groupingBy(booking -> booking.getItem().getId()));
    }

    private Map<Integer, List<Booking>> getNextBookings(Integer userId) {
        return bookingRepository.findFutureBookingOwnerApproved(LocalDateTime.now(), userId,
                        Sort.by(ASC, "start"))
                .stream().collect(groupingBy(booking -> booking.getItem().getId()));
    }

    private void setLastBooking(ItemResponseDto itemDto, List<Booking> pastBooking) {
        itemDto.setLastBooking(LastBookingMapper.bookingToLastBooking(Objects.requireNonNull(pastBooking
                .stream().findFirst().orElse(null))));
    }

    private void setNextBooking(ItemResponseDto itemDto, List<Booking> futureBooking) {
        itemDto.setNextBooking(NexBookingMapper.bookingToNexBooking(Objects.requireNonNull(futureBooking
                .stream().findFirst().orElse(null))));
    }

    private User getUserOrException(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId));
    }

    private Item getItemOrException(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId));
    }

}