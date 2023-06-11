package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;

import java.util.List;

public interface BookingService {
    @Transactional
    public OutputBookingDto create(InputBookingDto bookingDtoShort, long bookerId);

    @Transactional(readOnly = true)
    public OutputBookingDto findBookingById(Long bookingId, Long userId);

    @Transactional(readOnly = true)
    public List<OutputBookingDto> findAllBookingsByUser(String state, Long userId, Integer from, Integer size);

    @Transactional(readOnly = true)
    public List<OutputBookingDto> findAllBookingsByOwner(String state, Long ownerId, Integer from, Integer size);

    @Transactional
    public OutputBookingDto approve(long bookingId, long userId, Boolean approve);
}
