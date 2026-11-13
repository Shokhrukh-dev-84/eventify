package org.example.best.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Запрос на создания мероприятия")
public class EventCreateRequest {
    @Schema(description = "Название мероприятия", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 1, max = 100, message = "Название должно быть от 1 до 100 символов")
    private String title;

    @Schema(description = "Описание мероприятия")
    private String description;

    @Schema(description = "URL обложки мероприятия")
    private String coverUrl;

    @Schema(description = "Дата и время мероприятия", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Дата и время обязатеьны")
    private LocalDateTime dateTime;

    @Schema(description = "Общее число мест на мероприятие", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Количество мест объязательно для заполнения")
    @Min(value = 1, message = "Количество мест должно быть не менее 1")
    private Integer totalTickets;

}
