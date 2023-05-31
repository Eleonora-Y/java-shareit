package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1L)
            .build();

    private final List<ItemDto> itemsDtoList = List.of(
            new ItemDto(1L, "Name", "Description", true, null,
                    null, null, null),
            new ItemDto(2L, "Name2", "Description2", true, null,
                    null, null, null));
    private final CommentDto commentDto = CommentDto.builder().id(1L).text("Text").authorName("Name").build();

    @Test
    void createItemWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).create(1L, itemDto);
    }

    @Test
    void createItemWhenItemWithoutNameThenReturnedStatusIsBadRequest() throws Exception {
        ItemDto badItemDto = ItemDto.builder()
                .id(1L)
                .name("")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();

        Mockito.when(itemService.create(anyLong(), any()))
                .thenReturn(badItemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badItemDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).create(1L, badItemDto);
    }

    @Test
    void createItemWhenItemWithoutAvailbleThenReturnedStatusIsBadRequest() throws Exception {
        ItemDto badItemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("description")
                .requestId(1L)
                .build();
        Mockito.when(itemService.create(anyLong(), any()))
                .thenReturn(badItemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badItemDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).create(1L, badItemDto);
    }

    @Test
    void findItemByIdWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).findItemById(1L, 1L);
    }

    @Test
    void findItemByIdWhenItemIdIsNotFoundThenReturnedStatusIsNotfound() throws Exception {
        Mockito.when(itemService.findItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException(String.format("Item with id = %d not found.", 100L)));

        mvc.perform(get("/items/{id}", 100L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllUsersItemsWhenUserIdIsExistThenReturnedStatusIsOk() throws Exception {
        Mockito.when(itemService.findAllUsersItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemsDtoList);

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(itemsDtoList));

    }

    @Test
    void findAllUsersItemsWhenUserIdIsNotExistThenReturnedStatusIsNotFound() throws Exception {
        Mockito.when(itemService.findAllUsersItems(anyLong(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException(String.format("User with id = %d not found.", 100L)));

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 100L))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).findAllUsersItems(100L, 1, 1);
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto updateItemDto = ItemDto.builder()
                .id(1L)
                .name("updateItem")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();

        Mockito.when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(updateItemDto);

        mvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).update(updateItemDto, 1L, 1L);
    }

    @Test
    void searchItemByParams() throws Exception {
        Mockito.when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=descr", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Item")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Description")));
    }

    @Test
    void addCommentWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        when(itemService.addComment(any(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void addCommentWhenTextIsEmptyThenReturnedStatusIsBadRequest() throws Exception {
        CommentDto badCommentDto = CommentDto.builder().id(1L).text("").authorName("AuthorName").build();
        Mockito.when(itemService.addComment(any(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(badCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

}
