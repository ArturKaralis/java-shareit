package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * TODO Sprint add-item-requests.
 */

import ru.practicum.shareit.item.model.Item;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequestDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "requestor_id")
    private Long requestor;

    @Column(name = "created")
    private Instant created;

    @OneToMany
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private List<Item> answersList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemRequestDto that = (ItemRequestDto) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
