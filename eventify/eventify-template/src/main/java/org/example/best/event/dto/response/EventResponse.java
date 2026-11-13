package org.example.best.event.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Сведения о мероприятии")
public class EventResponse {
    @Schema(description = "Идентификатор мероприятия")
    private Long id;

    @Schema(description = "Название мероприятия")
    private String title;

    @Schema(description = "Описания мероприятия")
    private String description;

    @CreationTimestamp
    @Schema(description = "Дата и время начала мероприятия")
    private LocalDateTime dateTime;

    @Schema(description = "Общее число мест на мероприятие")
    private Integer totalTickets;

    @Schema(description = "Число мест, доступных к бронированию")
    private Integer availableTickets;

}
