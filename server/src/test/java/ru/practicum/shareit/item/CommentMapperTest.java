package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

    @Test
    public void shouldMakeCommentDto() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("text");
        comment.setAuthorName("name");
        CommentResponseDto commentDto = CommentMapper.makeCommentDto(comment);
        assertEquals(1, commentDto.getId());
        assertEquals("text", commentDto.getText());
        assertEquals("name", commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    public void shouldMakeCommentDtoList() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("text");
        comment.setAuthorName("name");
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        List<CommentResponseDto> commentsDto = CommentMapper.makeListCommentDto(comments);
        assertEquals(1, commentsDto.size());
        assertEquals(1, commentsDto.get(0).getId());
        assertEquals("text", commentsDto.get(0).getText());
        assertEquals("name", commentsDto.get(0).getAuthorName());
        assertEquals(comment.getCreated(), commentsDto.get(0).getCreated());
    }
}
