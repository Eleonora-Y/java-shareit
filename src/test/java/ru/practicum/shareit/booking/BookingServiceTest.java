package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;
    @Mock
    private BookingRepository bookingRepository;

    private final User user = new User(1L, "User", "user@email.com");
    private final UserDto userDto = new UserDto(1L, "User", "user@email.com");
    private final InputBookingDto inputBookingDto = InputBookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1L))
            .itemId(1L)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1L)
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .ownerId(1L)
            .build();

    private final OutputBookingDto outputBookingDto = OutputBookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1L))
            .item(itemDto)
            .build();
    private final Booking booking = Booking.builder()
            .booker(user)
            .id(1L)
            .status(BookingStatus.APPROVED)
            .item(item).build();


    @Test
    void createBooking_whenTimeIsNotValid_thenReturnedTimeDataException() {
        InputBookingDto bookingBadTime = InputBookingDto.builder()
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().minusHours(1L))
                .itemId(1L)
                .build();

        Exception e = assertThrows(TimeDataException.class,
                () -> bookingService.create(bookingBadTime, 1L));
        assertEquals(e.getMessage(), String.format("Invalid booking time start = %s  end = %s",
                bookingBadTime.getStart(), bookingBadTime.getEnd()));
    }

    @Test
    void createBooking_whenUserIsNotOwner_thenReturnedOperationAccessException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);
        Mockito.when(itemService.findOwnerId(anyLong()))
                .thenReturn(1L);

        Exception e = assertThrows(OperationAccessException.class,
                () -> bookingService.create(inputBookingDto, 1L));

        assertEquals(e.getMessage(), "The owner cannot be a booker.");
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenReturnedNotAvailableException() {
        itemDto.setAvailable(false);
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);
        Mockito.when(itemService.findOwnerId(anyLong()))
                .thenReturn(1L);
        Exception e = assertThrows(NotAvailableException.class,
                () -> bookingService.create(inputBookingDto, 2L));
        assertEquals(e.getMessage(), String.format("Item with id = %d is not available.", 1L));
    }

    @Test
    void findBookingById_whenBookingIsNotFound_thenReturnedNotFoundException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception e = assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(1L, 1L));

        assertEquals(e.getMessage(), String.format("Booking with id = %d not found.", 1L));
    }

    @Test
    void findBookingById_whenUserIsNotOwner_thenReturnedOperationAccessException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Exception e = assertThrows(OperationAccessException.class,
                () -> bookingService.findBookingById(1L, 100L));
        assertEquals(e.getMessage(), String.format("User with id = %d is not the owner, no access to booking.", 100L));
    }

    @Test
    void getAllBookings_whenStateIsUnknown_thenReturnedBadRequestException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Exception e = assertThrows(BadRequestException.class,
                () -> bookingService.findAllBookingsByUser("POI655", 1L, 0, 10));
        assertEquals(e.getMessage(), "Unknown state: POI655");
    }

    @Test
    void approve_whenBookingDecision_thenReturnedAlreadyExistsException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(itemService.findOwnerId(anyLong()))
                .thenReturn(1L);
        Exception e = assertThrows(AlreadyExistsException.class,
                () -> bookingService.approve(1L, 1L, true));
        assertEquals(e.getMessage(), "The booking decision has already been made.");
    }

    @Test
    void approve_whenUserIsNotOwner_thenReturnedOperationAccessException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(itemService.findOwnerId(anyLong()))
                .thenReturn(2L);
        Exception e = assertThrows(OperationAccessException.class,
                () -> bookingService.approve(1L, 1L, true));
        assertEquals(e.getMessage(), String.format("User with id = %d is not the owner, no access to booking.", 1L));
    }

    @Test
    void getAllBookingsByOwner_whenStateIsUnknown_thenReturnedBadRequestException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Exception e = assertThrows(BadRequestException.class,
                () -> bookingService.findAllBookingsByOwner("UNKNOWN_STATE", 1L, 0, 10));
        assertEquals(e.getMessage(), "Unknown state: UNKNOWN_STATE");
    }

}

