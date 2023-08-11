package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void findByItemId() {
        User user = new User();
        user.setName("userName");
        user.setEmail("user@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("text");
        comment.setItem(item);
        comment.setAuthorName("authorName");
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        assertEquals(1, comments.size());
        assertNotNull(comments.get(0).getId());
        assertEquals("text", comments.get(0).getText());
        assertNotNull(comments.get(0).getItem().getId());
        assertEquals("authorName", comments.get(0).getAuthorName());
        assertEquals(comment.getCreated(), comments.get(0).getCreated());
    }

}
