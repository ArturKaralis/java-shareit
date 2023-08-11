package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
public class RequestRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void findByRequesterId() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setRequesterId(2);
        itemRequestRepository.save(itemRequest);
        List<ItemRequest> itemRequestFromRepo = itemRequestRepository.findByRequesterId(itemRequest.getRequesterId(),
                Sort.by(DESC, "created"));
        assertEquals(1, itemRequestFromRepo.size());
        assertNotNull(itemRequestFromRepo.get(0).getId());
        assertEquals("description", itemRequestFromRepo.get(0).getDescription());
        assertEquals(2, itemRequestFromRepo.get(0).getRequesterId());
        assertEquals(itemRequest.getCreated(), itemRequestFromRepo.get(0).getCreated());
    }

}
