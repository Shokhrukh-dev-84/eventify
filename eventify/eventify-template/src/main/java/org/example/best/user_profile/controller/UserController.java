package org.example.best.user_profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.best.user_profile.dto.NotificationPreferences;
import org.example.best.user_profile.service.TelegramService;
import org.example.best.user_profile.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User", description = "Настройки уведомлений пользователя")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TelegramService telegramService;

    @GetMapping("/user/notifications")
    @Operation(
            summary = "Получить настройки уведомлений"
    )
    @ApiResponses(value = { @ApiResponse(
            responseCode = "200",
            description = "Настройки уведомлений",
            content = @Content(schema = @Schema(implementation = NotificationPreferences.class))
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )})
    public ResponseEntity<NotificationPreferences> userNotificationsGet () {
        NotificationPreferences preferences = userService.userNotificationsGet();
        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/user/notifications")
    @Operation(summary = "Обновить настройки уведомлений")
    @ApiResponses(value = { @ApiResponse(
                    responseCode = "200",
                    description = "Настройки обновлены",
                    content = @Content(schema = @Schema(implementation = NotificationPreferences.class))
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "404",
            description = "Настройки уведомлений не найдены",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )})
    public ResponseEntity<NotificationPreferences> userNotificationsPut(
            @Valid @RequestBody NotificationPreferences preferences) {
        NotificationPreferences updated = userService.userNotificationsPut(preferences);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/user/notifications")
    @Operation(summary = "Отменить уведомления")
    @ApiResponses(value = { @ApiResponse(
                    responseCode = "204",
                    description = "Настройки удалены"
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )})
    public ResponseEntity<Void> userNotificationsDelete() {
        userService.userNotificationsDelete();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("user/telegram/link")
    @Operation(summary = "Инициировать привязку Telegram")
    @ApiResponses(value = { @ApiResponse(
            responseCode = "200",
            description = "Код для /start команды в боте"
    ), @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    })
    public ResponseEntity<String> userTelegramLinkPost() {
        return ResponseEntity.ok(telegramService.getTelegramLink());
    }
}
