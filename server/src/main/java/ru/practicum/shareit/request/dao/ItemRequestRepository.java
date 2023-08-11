package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Integer> {

    @Query("select ir from ItemRequest ir where ir.requesterId <> ?1")
    Page<ItemRequest> findAllExceptUserId(Integer userId, Pageable pageable);

    List<ItemRequest> findByRequesterId(Integer requesterId, Sort sort);

}
