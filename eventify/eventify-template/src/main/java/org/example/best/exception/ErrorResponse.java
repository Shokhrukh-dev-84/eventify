package org.example.best.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.best.exception.type.ErrorLevel;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Сообщение о произошедшей ошибке")
public class ErrorResponse {

    @Schema(description = "Машинно-читаемый код ошибки", example = "VALIDATION_FAILED",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "Уровень серьёзности",
            example = "error",
            allowableValues = {"info", "warning", "error"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private ErrorLevel level;

    @Schema(description = "Человеко-читаемое сообщение",
            example = "Некоторые поля заполнены неверно")
    private String message;

    @Schema(description = "Детали ошибки (опционально)")
    private List<ErrorDetail> details;
}
