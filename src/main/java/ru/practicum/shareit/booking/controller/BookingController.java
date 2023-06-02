package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutputBookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @Valid @RequestBody InputBookingDto bookingDtoShort) {
        log.debug("POST-запрос на добавление нового бронирования.");
        return bookingService.create(bookingDtoShort, userId);
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.debug("GET-запрос на вывод бронирования по идентификатору.");
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<OutputBookingDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET-запрос на получения бронирований пользователя по идентификатору пользователя.");
        return bookingService.findAllBookingsByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET-запрос на получение бронирований владельца по идентификатору владельца.");
        return bookingService.findAllBookingsByOwner(state, userId, from, size);
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        log.debug("PATCH-запрос на обновление бронирования.");
        return bookingService.approve(bookingId, userId, approved);
    }

}