package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item create(Long userId, Item item);

    Optional<Item> get(Long itemId);

    List<Item> getAll(Long userId);

    Item update(Long userId, Long itemId, Item item);

    void delete(Long itemId);

    List<Item> search(String text);

    boolean checkItemId(Long itemId);
}
