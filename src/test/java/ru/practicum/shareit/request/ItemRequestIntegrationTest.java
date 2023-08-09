package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestIntegrationTest {
    private final ItemRequestService requestService;
    private final UserStorage userStorage;
    private final EntityManager entityManager;

    private static User user;
    private static User user2;
    private static CreateItemRequestDto requestDto;

    @BeforeAll
    static void beforeAll() {
        user = User.builder()
                .id(1L)
                .name("userName")
                .email("mail@ya.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("userName")
                .email("mail2@ya.ru")
                .build();

        requestDto = CreateItemRequestDto.builder()
                .description("need item")
                .build();
    }

    @Test
    void shouldCreateRequest() {
        userStorage.save(user);
        requestService.createRequest(user.getId(), requestDto);

        TypedQuery<ItemRequest> query = entityManager.createQuery("select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", 1L).getSingleResult();

        assertThat(requestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(user, equalTo(request.getRequester()));
        assertNull(request.getItems());
    }

    @Test
    void shouldGetRequestById() {
        userStorage.save(user);
        requestService.createRequest(user.getId(), requestDto);

        GetItemRequestDto request = requestService.getRequestById(user.getId(), 1L);

        assertThat(requestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(List.of(), equalTo(request.getItems()));
    }
}
