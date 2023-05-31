package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    @Override
    public OutputBookingDto create(InputBookingDto bookingDtoShort, long bookerId) {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart()) ||
                bookingDtoShort.getEnd().equals(bookingDtoShort.getStart())) {
            throw new TimeDataException(String
                    .format("Invalid booking time start = %s  end = %s",
                            bookingDtoShort.getStart(), bookingDtoShort.getEnd()));
        }
        User booker = UserMapper.toUser(userService.findUserById(bookerId));
        Item item = ItemMapper.toItem(itemService.findItemById(bookingDtoShort.getItemId(), bookerId));
        if (itemService.findOwnerId(item.getId()) == bookerId) {
            throw new OperationAccessException("The owner cannot be a booker.");
        }
        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDtoShort.getStart())
                    .end(bookingDtoShort.getEnd())
                    .item(item)
                    .booker(booker)
                    .status(BookingStatus.WAITING)
                    .build();
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new NotAvailableException(String.format("Item with id = %d is not available.", item.getId()));
        }
    }

    @Override
    @Transactional(readOnly=true)
    public OutputBookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id = %d not found.", bookingId)));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new OperationAccessException(String.format("User with id = %d is not the owner, no access to booking.", userId));
        }
    }

    @Override
    @Transactional(readOnly=true)
    public List<OutputBookingDto> findAllBookingsByUser(String state, Long userId, Integer from, Integer size) {
        userService.findUserById(userId);
        Pageable page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now, page));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, page));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, page));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now,
                                BookingStatus.WAITING, page));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED, page));

        }
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Override

    public List<OutputBookingDto> findAllBookingsByOwner(String state, Long ownerId, Integer from, Integer size) {
        userService.findUserById(ownerId);
        Pageable page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.findAllBookingsOwner(ownerId, page));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository.findAllCurrentBookingsOwner(ownerId, now, page));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository.findAllPastBookingsOwner(ownerId, now, page));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository.findAllFutureBookingsOwner(ownerId, now, page));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING, page));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllRejectedBookingsOwner(ownerId, BookingStatus.REJECTED, page));
        }
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Override

    public OutputBookingDto approve(long bookingId, long userId, Boolean approve) {
        OutputBookingDto booking = findBookingById(bookingId, userId);
        Long ownerId = itemService.findOwnerId(booking.getItem().getId());
        if (ownerId.equals(userId)
                && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AlreadyExistsException("The booking decision has already been made.");
        }
        if (!ownerId.equals(userId)) {
            throw new OperationAccessException(String.format("User with id = %d is not the owner, no access to booking.", userId));
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.update(BookingStatus.APPROVED, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.update(BookingStatus.REJECTED, bookingId);
        }
        return booking;
    }
}
