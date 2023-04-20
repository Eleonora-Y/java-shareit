package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final List<Item> items = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public Item create(Long userId, Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.add(item);
        return item;
    }

    @Override
    public Optional<Item> get(Long itemId) {
        return items.stream()
                .filter(item -> itemId.equals(item.getId()))
                .findFirst();
    }

    @Override
    public List<Item> getAll(Long userId) {
        return items.stream()
                .filter(item -> userId.equals(item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        return items.stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(text)
                                || item.getDescription().toLowerCase().contains(text)
                )
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        Item oldItem = get(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с этим идентификатором не найдена.")
        );
        if (!userId.equals(oldItem.getOwner().getId())) {
            throw new OperationAccessException("Пользователь с этим идентификатором не может форматировать эту вещь.");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return oldItem;
    }

    @Override
    public void delete(Long itemId) {
        items.remove(get(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с этим идентификатором не найдена.")
        ));
    }

    @Override
    public boolean checkItemId(Long itemId) {
        return items.stream().anyMatch(item -> itemId.equals(item.getId()));
    }
}
