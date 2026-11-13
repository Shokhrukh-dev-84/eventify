package org.example.best.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.best.auth.dto.AuthType;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Ответ на запрос авторизации")
public class AuthResponse {
    @Schema(description = "Токен авторизации")
    private String token;

    @Schema(description = "Роль пользователя")
    private AuthType role;
}
