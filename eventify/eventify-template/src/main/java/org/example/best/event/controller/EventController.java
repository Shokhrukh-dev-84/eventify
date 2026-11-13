package org.example.best.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.best.common.dto.pagination.Pageable;
import org.example.best.common.mapper.PageableMapper;
import org.example.best.common.dto.pagination.PageableEventResponse;
import org.example.best.event.dto.request.EventCreateRequest;
import org.example.best.event.dto.request.EventUpdateRequest;
import org.example.best.event.dto.response.EventResponse;
import org.example.best.event.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Tag(name = "Events", description = "Управление мероприятиями")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final PageableMapper pageableMapper;

    @PostMapping("/admin/events")
    @Operation(summary = "Создать мероприятие (Admin)")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(schema = @Schema(implementation = EventResponse.class))
    ), @ApiResponse(
            responseCode = "400",
            description = "Неверный запрос",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "403",
            description = "Доступ запрещён",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "409",
            description = "Конфликт",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<EventResponse> eventsPost(@Valid @RequestBody EventCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.eventsPost(request));
    }

    @PutMapping("/admin/events/{id}")
    @Operation(summary = "Обновить мероприятие (Admin)")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Updated",
            content = @Content(schema = @Schema(implementation = EventResponse.class))
    ), @ApiResponse(
            responseCode = "400",
            description = "Неверный запрос",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "403",
            description = "Доступ запрещён",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "404",
            description = "Ресурс не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "409",
            description = "Конфликт",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<EventResponse> eventsIdPut(@PathVariable Long id,
                                                     @Valid @RequestBody EventUpdateRequest request) {
        EventResponse response = eventService.eventsIdPut(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/events/{id}")
    @Operation(summary = "Удалить мероприятие(Admin)")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "204",
            description = "Мероприятие удалено"
    ), @ApiResponse(
            responseCode = "400",
            description = "Неверный запрос",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "403",
            description = "Доступ запрещён",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "404",
            description = "Ресурс не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "409",
            description = "Конфликт",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<Void> eventsIdDelete(@PathVariable Long id) {
        eventService.eventsIdDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events")
    @Operation(summary = "Получить список мероприятий")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Список мероприятий",
            content = @Content(schema = @Schema(implementation = PageableEventResponse.class))
    ), @ApiResponse(
            responseCode = "400",
            description = "Неверный запрос",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<PageableEventResponse> eventsGet(@RequestParam(value = "from",
                                                                   required = false)
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                           @RequestParam(value = "to",
                                                                   required = false)
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                           Pageable pageable) {
        org.springframework.data.domain.Pageable nativePageable = pageableMapper.toNativePageable(pageable);
        PageableEventResponse response = eventService.eventsGet(from, to, nativePageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Получить мероприятие по ID")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Мероприятие",
            content = @Content(schema = @Schema(implementation = EventResponse.class))
    ), @ApiResponse(
            responseCode = "400",
            description = "Неверный запрос",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "404",
            description = "Ресурс не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<EventResponse> eventsIdGet(@PathVariable Long id) {
        EventResponse response = eventService.eventsIdGet(id);
        return ResponseEntity.ok(response);

    }
}
