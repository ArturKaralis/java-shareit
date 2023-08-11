package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.impl.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemServiceImpl service;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    public void save() {
        User user = addUser("name1", "email1@mail.ru");
        ItemDto itemDto = makeItemDto("item1", "description1");
        service.save(itemDto, user.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName())
                .getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    public void update() {
        User user = addUser("name2", "email2@mail.ru");
        ItemDto itemDto = makeItemDto("item2", "description2");
        service.save(itemDto, user.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName())
                .getSingleResult();

        itemDto.setName("updateName");
        itemDto.setDescription("updateDescription");
        itemDto.setAvailable(false);
        service.update(itemDto, item.getId(), user.getId());
        TypedQuery<Item> query2 = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item updatedItem = query2.setParameter("id", item.getId())
                .getSingleResult();

        assertThat(updatedItem.getId(), notNullValue());
        assertThat(updatedItem.getName(), equalTo(itemDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    public void getById() {
        User user = addUser("name3", "email3@mail.ru");
        ItemDto itemDto = makeItemDto("item3", "description3");
        service.save(itemDto, user.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName())
                .getSingleResult();

        ItemResponseDto itemResponseDto = service.getById(item.getId(), user.getId());
        assertThat(itemResponseDto.getId(), equalTo(item.getId()));
        assertThat(itemResponseDto.getName(), equalTo(item.getName()));
        assertThat(itemResponseDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemResponseDto.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    public void getAll() {
        User user = addUser("name4", "email4@mail.ru");
        Integer from = 0;
        Integer size = 10;
        ItemDto itemDto = makeItemDto("item4", "description4");
        service.save(itemDto, user.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName())
                .getSingleResult();
        List<ItemResponseDto> items = service.getAll(user.getId(), from, size);

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getId(), equalTo(item.getId()));
        assertThat(items.get(0).getName(), equalTo(item.getName()));
        assertThat(items.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    public void search() {
        User user = addUser("name4", "email4@mail.ru");
        ItemDto itemDto = makeItemDto("item5", "description5");
        Integer from = 0;
        Integer size = 10;
        service.save(itemDto, user.getId());
        List<ItemResponseDto> items = service.search(itemDto.getName(), from, size);

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getId(), notNullValue());
        assertThat(items.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(items.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    public void saveComment() {
        User user = addUser("userName", "user@mail.ru");
        User booker = addUser("bookerName", "booker@mail.ru");
        Item item = addItem(user.getId());
        Booking booking = addBooking(item.getId(), booker.getId());
        bookingService.update(user.getId(), true, booking.getId());
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        CommentResponseDto commentResponseDto = service.saveComment(commentDto, item.getId(), user.getId());
        assertThat(commentResponseDto.getId(), notNullValue());
        assertThat(commentResponseDto.getText(), equalTo(commentDto.getText()));
        assertThat(commentResponseDto.getCreated(), notNullValue());
    }

    private ItemDto makeItemDto(String name, String description) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);
        itemDto.setRequestId(1);
        return itemDto;
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

    private Item addItem(Integer userId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("itemName");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        service.save(itemDto, userId);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        return query.setParameter("name", itemDto.getName())
                .getSingleResult();
    }

    private Booking addBooking(Integer itemId, Integer bookerId) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.parse("2023-01-02T12:13:14"));
        bookingDto.setEnd(LocalDateTime.parse("2023-02-02T12:13:14"));
        bookingDto.setItemId(itemId);
        bookingService.save(bookingDto, bookerId);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        return query.setParameter("id", bookerId).getSingleResult();
    }
}
