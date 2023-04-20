package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.getUserById(userId)));
        if (item.getId() != null && itemStorage.checkItemId(item.getId())) {
            throw new AlreadyExistsException("Вещь с этим идентификатором уже существует.");
        }
        return ItemMapper.toItemDto(itemStorage.create(userId, item));
    }

    public ItemDto get(Long itemId) {
        if (!itemStorage.checkItemId(itemId)) {
            throw new NotFoundException("Вещь с этим идентификатором не найдена.");
        }
        Item item = itemStorage.get(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с таким идентификатором не найдена.")
        );
        log.debug("ItemService: Item {} returned.", item);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getAll(Long userId) {
        return itemStorage.getAll(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.getUserById(userId)));
        if (!itemStorage.checkItemId(itemId)) {
            throw new NotFoundException("Вещь с этим идентификатором не найдена.");
        }
        return ItemMapper.toItemDto(itemStorage.update(userId, itemId, item));
    }

    public void delete(Long userId, Long itemId) {
        Item deleteItem = ItemMapper.toItem(get(itemId));
        if (!userId.equals(deleteItem.getOwner().getId())) {
            throw new OperationAccessException("Пользователь с этим идентификатором не может удалять эту вещь.");
        }
        itemStorage.delete(itemId);
    }

    public List<Item> search(String text) {
        if (text != null && !text.isBlank()) {
            List<Item> searchedItems = itemStorage.search(text.toLowerCase());
            return searchedItems;
        } else {
            return new ArrayList<>();
        }
    }
}
