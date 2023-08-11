package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentResponseDtoJsonTest {

    @Autowired
    private JacksonTester<CommentResponseDto> json;

    @Test
    public void testCommentDto() throws Exception {
        CommentResponseDto commentDto = new CommentResponseDto();
        commentDto.setId(1);
        commentDto.setText("text");
        commentDto.setAuthorName("name");
        commentDto.setCreated(LocalDateTime.parse("2023-01-02T12:13:14"));
        JsonContent<CommentResponseDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDto.getCreated().toString());
    }
}
