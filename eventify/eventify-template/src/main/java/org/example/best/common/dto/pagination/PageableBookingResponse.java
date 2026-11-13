package org.example.best.common.dto.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.example.best.booking.dto.response.BookingResponse;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "Страница с бронированиями")
public class PageableBookingResponse extends PageableAbstractResponse {

    private List<BookingResponse> content = new ArrayList<>();
}
