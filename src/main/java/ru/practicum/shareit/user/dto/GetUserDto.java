package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetUserDto {
    private Long id;
    private String name;
    private String email;
}
