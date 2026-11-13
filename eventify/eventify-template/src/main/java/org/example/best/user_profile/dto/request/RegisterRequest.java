package org.example.best.user_profile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на создание нового пользователя")
public class RegisterRequest {
    @Schema(description = "Электронная почта пользователя", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Пароль", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 8, max = 2147483647, message = "Длина пароля должна быть не меньше 8 и не больше 2147483647 символов")
    private String password;
}
