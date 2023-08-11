package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;

    @BeforeEach
    public void saveEntity() {
        user = new User();
        user.setName("userName");
        user.setEmail("user@mail.ru");
        userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setRequesterId(user.getId());
        itemRequestRepository.save(itemRequest);

        item = new Item();
        item.setName("itemName");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setItemRequest(itemRequest);
        itemRepository.save(item);
    }

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void findAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(1, items.size());
        assertNotNull(items.get(0).getId());
        assertEquals("itemName", items.get(0).getName());
        assertEquals("description", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
        assertNotNull(items.get(0).getOwner().getId());
    }

    @Test
    public void search() {
        List<Item> items = itemRepository.search(item.getName(), PageRequest.of(0, 10)).getContent();
        assertEquals(1, items.size());
        assertNotNull(items.get(0).getId());
        assertEquals("itemName", items.get(0).getName());
        assertEquals("description", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
        assertNotNull(items.get(0).getOwner().getId());
    }

    @Test
    public void findByRequestId() {
        List<Item> items = itemRepository.findByRequestId(item.getItemRequest().getId());
        assertEquals(1, items.size());
        assertNotNull(items.get(0).getId());
        assertEquals("itemName", items.get(0).getName());
        assertEquals("description", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
        assertNotNull(items.get(0).getOwner().getId());
    }

    @Test
    public void findAllByRequesterId() {
        List<Item> items = itemRepository.findAllByRequesterId(user.getId());
        assertEquals(1, items.size());
        assertNotNull(items.get(0).getId());
        assertEquals("itemName", items.get(0).getName());
        assertEquals("description", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
        assertNotNull(items.get(0).getOwner().getId());
    }

    @Test
    public void findAllNotEqualRequesterId() {
        List<Item> items = itemRepository.findAllByRequesterId(user.getId());
        assertEquals(1, items.size());
        assertNotNull(items.get(0).getId());
        assertEquals("itemName", items.get(0).getName());
        assertEquals("description", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
        assertNotNull(items.get(0).getOwner().getId());
    }

}
