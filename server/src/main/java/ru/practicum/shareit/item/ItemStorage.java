package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {

    @Query(value = "select i " +
            "from Item i " +
            "join fetch i.owner o " +
            "left join fetch i.request " +
            "left join fetch i.bookings " +
            "left join fetch i.comments " +
            "where o.id = :id ",
            countQuery = "select count(i) from Item i " +
                    "where i.owner.id = :id")
    Page<Item> findAllByOwnerIdWithBookings(@Param("id") @NonNull Long userId, Pageable pageable);

    @Query("select i " +
            "from Item i " +
            "join fetch i.owner " +
            "left join fetch i.request " +
            "left join fetch i.bookings " +
            "left join fetch i.comments " +
            "where i.id = :id ")
    @NonNull
    Optional<Item> findByIdWithOwner(@Param("id") @NonNull Long id);

    @Query(value = " select i from Item i " +
            "join fetch i.owner o " +
            "left join fetch i.request r " +
            "left join fetch i.bookings b " +
            "left join fetch i.comments c " +
            "where (lower(i.name) like lower(concat('%', :text, '%')) " +
            "   or lower(i.description) like lower(concat('%', :text, '%'))) " +
            "   and i.available = true ",
            countQuery = "select count(i) from Item i " +
                    "where (lower(i.name) like lower(concat('%', :text, '%')) " +
                    "   or lower(i.description) like lower(concat('%', :text, '%'))) " +
                    "   and i.available = true ")
    Page<Item> search(@Param("text") String text, Pageable pageable);
}
