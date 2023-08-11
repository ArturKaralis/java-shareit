package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestResponseDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestResponseDto> json;

    @Test
    public void testItemRequestDtoDto() throws Exception {
        ItemRequestResponseDto itemRequestDto = new ItemRequestResponseDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("description");
        itemRequestDto.setCreated(LocalDateTime.parse("2023-01-02T12:13:14"));
        JsonContent<ItemRequestResponseDto> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().toString());
        assertThat(result).extractingJsonPathNumberValue("$.items").isEqualTo(null);
    }
}
