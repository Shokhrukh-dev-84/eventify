package org.example.best.booking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на создания бронирования")
public class CreateBookingRequest {
    @Schema(description = "Идентификатор мероприятия", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long eventId;

    @Schema(description = "Количество билетов", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "Количество билетов не должно быть меньше 1")
    private Integer ticketCount;


}
