package org.example.best.common.dto.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.example.best.event.dto.response.EventResponse;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "Страница с мероприятиями")
public class PageableEventResponse extends  PageableAbstractResponse {
   private List<EventResponse> content = new ArrayList<>();
}
