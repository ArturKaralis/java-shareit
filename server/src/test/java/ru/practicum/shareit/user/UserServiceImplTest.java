package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

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
public class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    public void saveUser() {
        UserDto userDto = makeUserDto("name", "test@mail.ru");

        service.save(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void updateUser() {
        UserDto userDto = makeUserDto("name", "test@mail.ru");

        service.save(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();


        userDto.setName("updateName");
        userDto.setEmail("update@mail.ru");

        service.update(userDto, user.getId());

        TypedQuery<User> query2 = em.createQuery("Select u from User u where u.id = :id", User.class);
        User updatedUser = query2.setParameter("id", user.getId())
                .getSingleResult();

        assertThat(updatedUser.getId(), notNullValue());
        assertThat(updatedUser.getName(), equalTo(userDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void getAllUsers() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("Ivan","ivan@email"),
                makeUserDto("Petr", "petr@email"),
                makeUserDto("Vasilii", "vasilii@email")
        );

        for (UserDto userDto : sourceUsers) {
            User user = UserMapper.makeUser(userDto);
            em.persist(user);
        }
        em.flush();

        List<UserDto> targetUsers = service.getAll();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void getUserById() {
        UserDto userDto = makeUserDto("name", "test@mail.ru");

        service.save(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        UserDto userCheck = service.getById(user.getId());

        assertThat(userCheck.getId(), notNullValue());
        assertThat(userCheck.getId(), equalTo(user.getId()));
        assertThat(userCheck.getName(), equalTo(userDto.getName()));
        assertThat(userCheck.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void delete() {
        UserDto userDto1 = makeUserDto("name1", "test1@mail.ru");
        UserDto userDto2 = makeUserDto("name2", "test2@mail.ru");
        service.save(userDto1);
        service.save(userDto2);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query.setParameter("email", userDto1.getEmail()).getSingleResult();
        User user2 = query.setParameter("email", userDto2.getEmail()).getSingleResult();
        service.delete(user1.getId());
        List<UserDto> users = service.getAll();
        assertThat(users, hasSize(1));
        assertThat(users.get(0).getId(), equalTo(user2.getId()));
        assertThat(users.get(0).getName(), equalTo(user2.getName()));
        assertThat(users.get(0).getEmail(), equalTo(user2.getEmail()));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }
}
