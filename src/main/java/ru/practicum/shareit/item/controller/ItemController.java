package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.debug("POST-запрос на создание новой вещи.");
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.debug("GET-запрос на получение вещи по идентификатору.");
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET-запрос на получение всех вещей пользователя по идентификатору.");
        return itemService.findAllUsersItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("PATCH-запрос на обновление вещи.");
        return itemService.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.debug("DELETE-запрос на удаление вещи.");
        itemService.deleteById(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.debug("GET-запрос на поиск вещей.", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.debug("POST-запрос на добавление отзыва.");
        return itemService.addComment(itemId, userId, commentDto);
    }
}