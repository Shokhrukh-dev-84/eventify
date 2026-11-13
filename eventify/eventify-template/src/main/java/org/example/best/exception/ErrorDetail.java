package org.example.best.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Сведения о произошедшей ошибке")
public class ErrorDetail {

    @Schema(description = "Поле который содержит ошибку", example = "email")
    private String field;

    @Schema(description = "Описание ошибки", example = "Должен быть валидным email адресом")
    private String message;
}
