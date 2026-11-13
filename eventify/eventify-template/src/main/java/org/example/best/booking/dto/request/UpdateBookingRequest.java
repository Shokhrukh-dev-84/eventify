package org.example.best.booking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на изменения бронирования")
public class UpdateBookingRequest {
    @Schema(description = "Количество билетов")
    private Integer ticketCount;
}
