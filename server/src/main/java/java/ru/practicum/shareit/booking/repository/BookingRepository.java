package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Query("UPDATE Booking b " +
            "SET b.status = :status  " +
            "WHERE b.id = :bookingId")
    void update(BookingStatus status, Long bookingId);

    //запросы по юзеру

    List<Booking> findAllByBookerIdOrderByStartDesc(long id, Pageable page);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Long id, BookingStatus status, Pageable page);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long id,
                                                                                 LocalDateTime end,
                                                                                 LocalDateTime start,
                                                                                 Pageable page);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime start,
                                                                              BookingStatus status,
                                                                              Pageable page);

    //запросы по хозяину
    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.ownerId = :ownerId " +
            "order by b.start desc")
    List<Booking> findAllBookingsOwner(Long ownerId, Pageable page);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.ownerId = :ownerId " +
            "and :time between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllCurrentBookingsOwner(Long ownerId, LocalDateTime time, Pageable page);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.ownerId = :ownerId " +
            "and b.end < :time " +
            "order by b.start desc")
    List<Booking> findAllPastBookingsOwner(Long ownerId, LocalDateTime time, Pageable page);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.ownerId = :ownerId " +
            "and b.start > :time " +
            "order by b.start desc")
    List<Booking> findAllFutureBookingsOwner(Long ownerId, LocalDateTime time, Pageable page);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.ownerId = :ownerId " +
            "and b.start > :time and b.status = :status " +
            "order by b.start desc")
    List<Booking> findAllWaitingBookingsOwner(Long ownerId, LocalDateTime time, BookingStatus status, Pageable page);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.ownerId = :ownerId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findAllRejectedBookingsOwner(Long ownerId, BookingStatus status, Pageable page);

    // для item
    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.id = :itemId " +
            "order by b.start desc")
    List<Booking> findAllBookingsItem(Long itemId);

    //для comment

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndIsBefore(Long itemId,
                                                                      Long bookerId,
                                                                      BookingStatus status,
                                                                      LocalDateTime time);
}