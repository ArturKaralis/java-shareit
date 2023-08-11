package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestServiceImpl service;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;

    @Test
    public void save() {
        User user = addUser("name1", "email1@email.ru");
        ItemRequestDto itemRequestDto = makeItemRequestDto("description");
        service.save(user.getId(), itemRequestDto);
        TypedQuery<ItemRequest> query = em.createQuery("select i from ItemRequest i " +
                "where i.requesterId = :requesterId", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("requesterId", user.getId()).getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequesterId(), equalTo(user.getId()));
        assertThat(itemRequest.getCreated(), notNullValue());
    }

    @Test
    public void findAll() {
        User user = addUser("name2", "email2@email.ru");
        User requester = addUser("name3", "email3@email.ru");
        Integer from = 0;
        Integer size = 10;
        List<ItemRequestDto> sourceRequests = List.of(
                makeItemRequestDto("description 1"),
                makeItemRequestDto("description 2"),
                makeItemRequestDto("description 3"));
        for (ItemRequestDto itemRequestDto : sourceRequests) {
            ItemRequest itemRequest = ItemRequestMapper.makeItemRequest(itemRequestDto);
            itemRequest.setRequesterId(requester.getId());
            em.persist(itemRequest);
        }
        em.flush();
        List<ItemRequestResponseDto> targetRequests = service.findAll(user.getId(), from, size);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDto sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", notNullValue())
            )));
        }
    }

    @Test
    public void findAllOwn() {
        User user = addUser("name3", "email3@email.ru");
        List<ItemRequestDto> sourceRequests = List.of(
                makeItemRequestDto("description 1"),
                makeItemRequestDto("description 2"),
                makeItemRequestDto("description 3"));
        for (ItemRequestDto itemRequestDto : sourceRequests) {
            ItemRequest itemRequest = ItemRequestMapper.makeItemRequest(itemRequestDto);
            itemRequest.setRequesterId(user.getId());
            em.persist(itemRequest);
        }
        em.flush();
        List<ItemRequestResponseDto> targetRequests = service.findAllOwn(user.getId());

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDto sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", notNullValue())
            )));
        }
    }

    @Test
    public void findById() {
        User user = addUser("name4", "email4@email.ru");
        ItemRequestDto itemRequestDto = makeItemRequestDto("description");
        service.save(user.getId(), itemRequestDto);
        TypedQuery<ItemRequest> query = em.createQuery("select i from ItemRequest i " +
                "where i.requesterId = :requesterId", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("requesterId", user.getId()).getSingleResult();

        ItemRequestResponseDto itemRequestResponseDto = service.findById(user.getId(), itemRequest.getId());
        assertThat(itemRequestResponseDto.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestResponseDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestResponseDto.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    public void setItemsForAllOwn() {
        User user = addUser("userName", "useer@mail.ru");
        ItemDto itemDto = new ItemDto();
        itemDto.setName("itemName");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(11);
        itemService.save(itemDto, user.getId());
        TypedQuery<Item> query = em.createQuery("select i from Item i " +
                "where i.owner.id = :ownerId", Item.class);
        Item item = query.setParameter("ownerId", user.getId()).getSingleResult();
        ItemRequestDto itemRequestDto = makeItemRequestDto("description");
        itemRequestService.save(user.getId(), itemRequestDto);
        TypedQuery<ItemRequest> query2 = em.createQuery("select ir from ItemRequest ir " +
                "where ir.description = :description", ItemRequest.class);
        ItemRequest itemRequest = query2.setParameter("description", itemRequestDto.getDescription())
                .getSingleResult();
        item.setItemRequest(itemRequest);
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.makeItemRequestDto(itemRequest);
        List<ItemRequestResponseDto> itemsRequestsDto = new ArrayList<>();
        itemsRequestsDto.add(itemRequestResponseDto);
        List<ItemRequestResponseDto> addItem = service.setItemsForAllOwn(itemsRequestsDto, user.getId());
        assertThat(addItem, hasSize(1));
        assertThat(addItem.get(0).getId(), notNullValue());
        assertThat(addItem.get(0).getDescription(), equalTo(itemRequestResponseDto.getDescription()));
        assertThat(addItem.get(0).getItems(), hasSize(1));
    }

    @Test
    public void setItemsForAll() {
        User user1 = addUser("userName1", "useer1@mail.ru");
        User user2 = addUser("userName2", "useer2@mail.ru");
        ItemDto itemDto = new ItemDto();
        itemDto.setName("itemName");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(11);
        itemService.save(itemDto, user1.getId());
        TypedQuery<Item> query1 = em.createQuery("select i from Item i " +
                "where i.owner.id = :ownerId", Item.class);
        Item item = query1.setParameter("ownerId", user1.getId()).getSingleResult();
        ItemRequestDto itemRequestDto = makeItemRequestDto("description1");
        itemRequestService.save(user1.getId(), itemRequestDto);
        TypedQuery<ItemRequest> query2 = em.createQuery("select ir from ItemRequest ir " +
                "where ir.description = :description", ItemRequest.class);
        ItemRequest itemRequest = query2.setParameter("description", itemRequestDto.getDescription())
                .getSingleResult();
        item.setItemRequest(itemRequest);
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.makeItemRequestDto(itemRequest);
        List<ItemRequestResponseDto> itemsRequestsDto = new ArrayList<>();
        itemsRequestsDto.add(itemRequestResponseDto);
        List<ItemRequestResponseDto> addItem = service.setItemsForAll(itemsRequestsDto, user2.getId());
        assertThat(addItem, hasSize(1));
        assertThat(addItem.get(0).getId(), notNullValue());
        assertThat(addItem.get(0).getDescription(), equalTo(itemRequestResponseDto.getDescription()));
        assertThat(addItem.get(0).getItems(), hasSize(1));
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);
        return itemRequestDto;
    }

    private User addUser(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        userService.save(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        return query.setParameter("email", userDto.getEmail())
                .getSingleResult();
    }
}
