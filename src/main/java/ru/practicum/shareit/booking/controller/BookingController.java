package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutputBookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @Valid @RequestBody InputBookingDto bookingDtoShort) {
        return bookingService.create(bookingDtoShort, userId);
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<OutputBookingDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwner(state, userId);
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }

}