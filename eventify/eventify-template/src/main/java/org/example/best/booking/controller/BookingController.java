package org.example.best.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.best.booking.dto.request.CreateBookingRequest;
import org.example.best.booking.dto.request.UpdateBookingRequest;
import org.example.best.booking.dto.response.BookingResponse;
import org.example.best.booking.service.BookingService;
import org.example.best.common.dto.pagination.PageableBookingResponse;
import org.example.best.exception.ErrorResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Bookings", description = "Операция с бронированиями")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/bookings/{id}")
    @Operation(summary = "Получить бронирование по ID")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = BookingResponse.class))
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
            description = "Бронирование не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<BookingResponse> bookingsIdGet(@PathVariable Long id) {

        BookingResponse response = bookingService.bookingsIdGet(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/bookings/{id}")
    @Operation(summary = "Обновить бронирование")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Updated",
            content = @Content(schema = @Schema(implementation = BookingResponse.class))
    ), @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации",
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
            description = "Бронирование не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<BookingResponse> bookingsIdPut(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateBookingRequest request) {
        BookingResponse response = bookingService.bookingsIdPut(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/bookings/{id}")
    @Operation(summary = "Отменить бронирование")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "204",
            description = "Бронирование отменено"
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
            description = "Бронирование не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<Void> bookingsIdDelete(@PathVariable Long id) {
        bookingService.bookingsIdDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings")
    @Operation(summary = "Получить свои бронирования")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = BookingResponse.class))
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<List<BookingResponse>> bookingsGet() {
        List<BookingResponse> bookings = bookingService.bookingsGet();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/bookings")
    @Operation(summary = "Создать бронирование")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(schema = @Schema(implementation = BookingResponse.class))
    ), @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации",
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
            description = "Мероприятие не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<BookingResponse> bookingsPost(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.bookingsPost(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/bookings/{id}/confirm")
    @Operation(summary = "Подтвердить бронирование(Admin)")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "204",
            description = "Бронирование подтверждено"
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
            description = "Бронирование не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<Void> adminBookingsIdConfirm(@PathVariable Long id) {
        bookingService.adminBookingsIdConfirm(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/bookings")
    @Operation(summary = "Получить все бронирование(Admin)")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Страница с бронированиями",
            content = @Content(schema = @Schema(implementation = PageableBookingResponse.class))
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "403",
            description = "Доступ запрещён",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<PageableBookingResponse> adminBookingsGet(@RequestParam(value = "eventId", required = false) Long eventId,
                                                                    @RequestParam(value = "unconfirmedOnly", required = false) Boolean unconfirmedOnly,
                                                                    Pageable pageable) {
        PageableBookingResponse response = bookingService.adminBookingsGet(
                eventId,
                unconfirmedOnly,
                pageable);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/bookings/{id}")
    @Operation(summary = "Удалить бронирование(Admin)")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "204",
            description = "Бронирование удалено"
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
            description = "Бронирование не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<Void> adminBookingsIdDelete(@PathVariable Long id) {
        bookingService.adminBookingsIdDelete(id);
        return ResponseEntity.noContent().build();
    }
}
