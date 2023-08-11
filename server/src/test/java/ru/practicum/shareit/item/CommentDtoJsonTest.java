package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    public void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }
}
