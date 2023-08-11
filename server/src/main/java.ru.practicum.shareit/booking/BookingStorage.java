package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enam.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.id = :id ")
    @NotNull Optional<Booking> findById(@Param("id") @NotNull Long id);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker bk " +
            "join fetch b.item " +
            "where bk = :user ",
            countQuery = "select b from Booking b " +
                    "where b.booker = :user ")
    List<Booking> findByBooker(@Param("user") User booker, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker bk " +
            "join fetch b.item " +
            "where bk = :user " +
            "   and b.startDate < :time " +
            "   and b.endDate > :time",
            countQuery = "select b from Booking b " +
                    "where b.booker = :user " +
                    "   and b.startDate < :time " +
                    "   and b.endDate > :time")
    List<Booking> findByBookerCurrent(@Param("user") User booker, @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker bk " +
            "join fetch b.item " +
            "where bk = :user " +
            "   and b.endDate < :time",
            countQuery = "select b from Booking b " +
                    "where b.booker = :user " +
                    "   and b.endDate < :time")
    List<Booking> findByBookerPast(@Param("user") User booker, @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker bk " +
            "join fetch b.item " +
            "where bk = :user " +
            "   and b.startDate > :time",
            countQuery = "select b from Booking b " +
                    "where b.booker = :user " +
                    "   and b.startDate > :time")
    List<Booking> findByBookerFuture(@Param("user") User booker, @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker bk " +
            "join fetch b.item " +
            "where bk = :user " +
            "   and b.status = :status",
            countQuery = "select b from Booking b " +
                    "where b.booker = :user " +
                    "   and b.status = :status")
    List<Booking> findByBookerAndStatus(@Param("user") User booker, @Param("status") BookingStatus status, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.item.owner = :user ",
            countQuery = "select b from Booking b " +
                    "where b.item.owner= :user ")
    List<Booking> findByItemOwner(@Param("user") User itemOwner, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker bk " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.startDate < :time " +
            "   and b.endDate > :time",
            countQuery = "select b from Booking b " +
                    "where b.item.owner = :user " +
                    "   and b.startDate < :time " +
                    "   and b.endDate > :time")
    List<Booking> findByItemOwnerCurrent(@Param("user") User itemOwner, @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker bk " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.endDate < :time",
            countQuery = "select b from Booking b " +
                    "where b.item.owner = :user " +
                    "   and b.endDate < :time")
    List<Booking> findByItemOwnerPast(@Param("user") User itemOwner, @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker bk " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.startDate > :time",
            countQuery = "select b from Booking b " +
                    "where b.item.owner = :user " +
                    "   and b.startDate > :time")
    List<Booking> findByItemOwnerFuture(@Param("user") User itemOwner, @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "join fetch b.booker bk " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.status = :status",
            countQuery = "select b from Booking b " +
                    "where b.item.owner = :user " +
                    "   and b.status = :status")
    List<Booking> findByItemOwnerAndStatus(@Param("user") User itemOwner, @Param("status") BookingStatus status, Pageable pageable);

    @Query(value = "select count(b)" +
            "from Booking b " +
            "where b.booker = ?1 " +
            "and b.item = ?2 " +
            "and b.endDate < ?3 " +
            "and b.status = ?4 ")
    Long bookingsBeforeNowCount(User booker, Item item, LocalDateTime nowTime, BookingStatus status);
}
