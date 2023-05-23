package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final UserDto userDto = new UserDto(1L, "User", "user@email.com");

    @Test
    void createUser_whenUserDtoValid_thenReturnedStatusIsOk() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).create(userDto);
    }

    @Test
    void createUser_whenUserDtoNotValid_thenReturnedBadRequest() throws Exception {
        UserDto userDto2 = new UserDto(2L, "", "user2@email.ru");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(userDto);
    }

    @Test
    void findById_whenUserIsExist_thenReturnedStatusIsOk() throws Exception {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);

        String result = mvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(userDto));
    }

    @Test
    void findById_whenUserIsNotExist_thenReturnedStatusIsNotFound() throws Exception {
        Mockito.when(userService.findUserById(100L))
                .thenThrow(new NotFoundException(String.format("User with id = %d not found.", 1L)));

        mvc.perform(get("/users/{id}", 100L))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllUsersTest() throws Exception {
        Mockito.when(userService.findAllUsers())
                .thenReturn(List.of(userDto));

        String result = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(userDto)));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }
}
