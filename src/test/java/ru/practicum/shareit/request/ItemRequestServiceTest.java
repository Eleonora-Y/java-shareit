package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepository;
    private final User user = new User(1L, "User", "user@email.com");
    private final UserDto userDto = new UserDto(1L, "User", "user@email.com");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requester(user)
            .description("description")
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .requester(userDto)
            .items(new ArrayList<>())
            .build();

    @Test
    void createRequest_whenUserIsExist_thenReturnedExpectedRequest() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);

        Mockito.when(requestRepository.save(any()))
                .thenReturn(itemRequest);
        assertEquals(requestService.create(itemRequestDto, 1L), itemRequestDto);
    }

    @Test
    void createRequest_whenUserIsNotExist_thenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User with id = %d not found.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.create(itemRequestDto, 100L));
        assertEquals(e.getMessage(), String.format("User with id = %d not found.", 1L));
    }

    @Test
    void findById_whenRequestIsValid_thenReturnedExpectedRequest() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(requestRepository.findById(any()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(itemRepository.findAllByItemRequest(any()))
                .thenReturn(new ArrayList<>());
        assertEquals(requestService.findById(1L, 1L), itemRequestDto);
    }

    @Test
    void findById_whenRequestIsNotExist_thenReturnedNotFoundException() {
        Mockito.when(requestRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Request with id = %d not found.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.findById(1L, 1L));
        assertEquals(e.getMessage(), String.format("Request with id = %d not found.", 1L));
    }

    @Test
    void findAllRequests_whenParamsIsExist_thenReturnedExpectedListRequests() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemRepository.findAllByItemRequest(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(requestRepository.findAllByRequesterIdIsNot(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        assertEquals(requestService.findAllRequests(1L, 1, 1), List.of(itemRequestDto));
    }

    @Test
    void findAllRequests_whenUserIsNotExist_thenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User with id = %d not found.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.findAllRequests(1L, 1, 1));
        assertEquals(e.getMessage(), String.format("User with id = %d not found.", 1L));
    }


    @Test
    void findAllUserRequests_whenUserIsExist_thenReturnedExpectedListRequests() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByItemRequest(any()))
                .thenReturn(new ArrayList<>());
        assertEquals(requestService.findAllUserRequests(1L), List.of(itemRequestDto));
    }

    @Test
    void findAllUserRequests_whenUserIsNotExist_thenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User with id = %d not found.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.findAllUserRequests(1L));
        assertEquals(e.getMessage(), String.format("User with id = %d not found.", 1L));
    }

}