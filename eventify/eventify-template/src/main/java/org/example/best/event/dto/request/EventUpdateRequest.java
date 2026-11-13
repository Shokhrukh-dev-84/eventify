package org.example.best.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на изменения сведений о мероприятии")
public class EventUpdateRequest {

    @Schema(description = "название мероприятия")
    @Size(min = 0, max = 100)
    private String title;

    @Schema(description = "Описание мероприятия")
    private String description;

    @Schema(description = "Дата и время начала мероприятия")
    private LocalDateTime dateTime;

    @Schema(description = "Общее число мест на мероприятие")
    @Min(value = 1)
    private Integer totalTickets;

    @Schema(description = "URL обложки")
    private String coverUrl;
}
