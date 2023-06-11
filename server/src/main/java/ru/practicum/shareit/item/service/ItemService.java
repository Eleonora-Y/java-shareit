package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    public ItemDto create(Long userId, ItemDto itemDto);

    public ItemDto findItemById(Long itemId, Long userId);

    public List<ItemDto> findAllUsersItems(Long userId, Integer from, Integer size);

    public ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    public ItemDto updateBookings(ItemDto itemDto);

    public void deleteById(Long itemId);

    public List<ItemDto> search(String text, Integer from, Integer size);

    public Long findOwnerId(Long itemId);

    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
