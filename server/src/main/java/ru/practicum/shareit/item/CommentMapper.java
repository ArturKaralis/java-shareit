package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentResponseDto makeCommentDto(Comment comment) {
        CommentResponseDto commentDto = new CommentResponseDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static List<CommentResponseDto> makeListCommentDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::makeCommentDto).collect(Collectors.toList());
    }

}
