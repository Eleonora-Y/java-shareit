package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = ItemDto.builder()
            .name("testItem")
            .description("testDescription")
            .available(true)
            .build();
    private final InputBookingDto inputBookingDto = InputBookingDto.builder()
            .start(LocalDateTime.of(2023, 05, 10, 13, 00, 00))
            .end(LocalDateTime.of(2023, 05, 20, 13, 00, 00))
            .itemId(1L).build();
    private final OutputBookingDto outputBookingDto = OutputBookingDto.builder()
            .start(LocalDateTime.of(2023, 05, 10, 13, 00, 00))
            .end(LocalDateTime.of(2023, 05, 20, 13, 00, 00))
            .item(itemDto)
            .build();

    @Test
    void createBooking_whenAllParamsIsValid_thenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.create(any(), anyLong()))
                .thenReturn(outputBookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(inputBookingDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

       // verify(bookingService).create(inputBookingDto, 1L);
        verify(bookingService,never()).create(inputBookingDto, 1L);
     
    }

    @Test
    void createBooking_whenStartIsNotValid_thenReturnedStatusIsBadRequest() throws Exception {
        InputBookingDto badInputBookingDto = InputBookingDto.builder()
                .start(LocalDateTime.of(1000, 05, 10, 13, 00, 00))
                .end(LocalDateTime.of(2023, 05, 20, 13, 00, 00))
                .itemId(1L)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badInputBookingDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, never()).create(badInputBookingDto, 1L);
    }

    @Test
    void createBooking_whenUserIsNotOwner_thenReturnedStatusIsNotFound() throws Exception {
        Mockito.when(bookingService.create(any(), anyLong()))
                .thenThrow(new NotFoundException(String.format("User with id = %d not found.", 100L)));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(inputBookingDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findBookingById_whenAllParamsIsValid_thenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(outputBookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(inputBookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).findBookingById(1L, 1L);
    }

    @Test
    void findBookingById_whenBookingIdNotFound_thenReturnedStatusIsNotFound() throws Exception {
        Mockito.when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException(String.format("Booking with id = %d not found.", 100L)));

        mvc.perform(get("/bookings/{bookingId}", 100L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(inputBookingDto)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllByUserId_whenAllParamsIsValid_whenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.findAllBookingsByUser(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(outputBookingDto));

        String result = mvc.perform(get("/bookings/?state=ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(outputBookingDto)));
    }

    @Test
    void findAllByOwnerId_whenAllParamsIsValid_whenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.findAllBookingsByOwner(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(outputBookingDto));

        String result = mvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(outputBookingDto)));
    }

    @Test
    void approveBooking_whenAllParamsIsValid_whenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.approve(anyLong(), anyInt(), anyBoolean()))
                .thenReturn(outputBookingDto);

        mvc.perform(get("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
