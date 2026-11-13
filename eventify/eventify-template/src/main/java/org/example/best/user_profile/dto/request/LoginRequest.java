package org.example.best.user_profile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на авторизацию пользователя")
public class LoginRequest {
    @Schema(description = "Электронная почта пользователя", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Пароль", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;




}
